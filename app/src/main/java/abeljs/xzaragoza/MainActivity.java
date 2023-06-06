package abeljs.xzaragoza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import com.smarteist.autoimageslider.SliderView;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import abeljs.xzaragoza.adaptadores.SliderNoticiasAdapter;
import abeljs.xzaragoza.apis.BusquedaNoticiasAPI;
import abeljs.xzaragoza.apis.BusquedaNoticiasCallback;
import abeljs.xzaragoza.apis.BusquedaTemperaturaAPI;
import abeljs.xzaragoza.apis.BusquedaTemperaturaCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;
import abeljs.xzaragoza.data.FavoritosDao;
import abeljs.xzaragoza.data.Noticias;
import abeljs.xzaragoza.data.NoticiasDao;
import abeljs.xzaragoza.data.Postes;
import abeljs.xzaragoza.data.PostesDao;
import abeljs.xzaragoza.data.Temperatura;
import abeljs.xzaragoza.fragments.FragmentBuses;
import abeljs.xzaragoza.fragments.FragmentFavoritos;
import abeljs.xzaragoza.fragments.FragmentTiemposPoste;
import abeljs.xzaragoza.servicios.CargaBusesService;

public class MainActivity extends AppCompatActivity {

    public static final int DIAS_CON_NOTICIA = -14;
    Context contexto;
    EditText edtNPoste;
    TextView txtNombrePoste;
    RadioGroup radioGroup;
    RadioButton rbBuses;
    RadioButton rbFavoritos;
    SliderView sldNoticias;
    ConstraintLayout clTiempo;

    // Temperatura
    TextView txtTempActual;
    TextView txtTempMin;
    TextView txtTempMax;
    TextView txtEstadoCielo;

    private SliderNoticiasAdapter sliderNoticiasAdapter;
    private TextWatcher textChangedListener;
    private int ultimoChk = 1; // 1 = Buses, 2 = Favoritos
    private RadioGroup.OnCheckedChangeListener checkedChangeListenerPestanyas;
    private Handler handlerCargaPoste = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, CargaBusesService.class));
        contexto = this;


        inicializarVistas();
        inicializarEventos();

        BusquedaTemperaturaAPI temperaturaAPI = new BusquedaTemperaturaAPI();
        temperaturaAPI.getTemperatura(new BusquedaTemperaturaCallback() {
            @Override
            public void onBusquedaTemperaturaComplete(Temperatura result) {
                clTiempo.post(new Runnable() {
                    @Override
                    public void run() {
                        txtTempActual.setText(String.valueOf(result.temperaturaActual));
                        txtTempMin.setText(String.valueOf(result.temperaturaMinima) + "º");
                        txtTempMax.setText(String.valueOf(result.temperaturaMaxima) + "º");
                        String estadoTiempoCadena = result.estadoDeCielo;

                        clTiempo.setClipToOutline(true);

                        if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*torment.*")) {
                            clTiempo.setBackgroundResource(R.drawable.bg_tormenta);
                        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*lluvi.*")) {
                            clTiempo.setBackgroundResource(R.drawable.bg_lluvia);
                        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*nub.*") ||
                                comprobarExpresionRegular(estadoTiempoCadena, "(?i).*cubierto.*")) {
                            clTiempo.setBackgroundResource(R.drawable.bg_nublado);
                        } else if (comprobarExpresionRegular(estadoTiempoCadena, "(?i).*niebl.*")) {
                            clTiempo.setBackgroundResource(R.drawable.bg_niebla);
                        }
                        txtEstadoCielo.setText(estadoTiempoCadena);
                        clTiempo.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onBusquedaTemperaturaError(String cadenaError) {
            }
        });

        BusquedaNoticiasAPI noticiasAPI = new BusquedaNoticiasAPI();
        noticiasAPI.getNoticias(new BusquedaNoticiasCallback() {
            @Override
            public void onBusquedaNoticiasComplete(List<Noticias> listaNoticias) {
                BaseDeDatos db = Room.databaseBuilder(contexto,
                        BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
                NoticiasDao daoNoticias = db.daoNoticias();

                for (Noticias noticia : listaNoticias) {
                    daoNoticias.insertarNoticia(noticia);
                }

                Calendar fechaBorrado = Calendar.getInstance();
                fechaBorrado.add(Calendar.DATE, DIAS_CON_NOTICIA);

                daoNoticias.limpiarNoticias(fechaBorrado.getTime());

                List<Noticias> listaCincoNoticias = daoNoticias.getCincoNoticiasRecientes();
                for (Noticias noticia : listaCincoNoticias) {
                    noticia.setFechaVista(Calendar.getInstance().getTime());
                    daoNoticias.cambiarFechaVista(noticia);
                }
                sldNoticias.post(new Runnable() {
                    @Override
                    public void run() {
                        sliderNoticiasAdapter = new SliderNoticiasAdapter(contexto, listaCincoNoticias);
                        sldNoticias.setSliderAdapter(sliderNoticiasAdapter);
                        sldNoticias.setAutoCycle(true);
                        sldNoticias.startAutoCycle();

                    }
                });
            }

            @Override
            public void onBusquedaNoticiasError(String cadenaError) {

            }
        });


        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        FavoritosDao daoFavoritos = db.daoFavoritos();

        if (daoFavoritos.getFavoritos().size() > 0) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContenedorFragments, new FragmentFavoritos())
                    .commit();
            ultimoChk = 2;
            rbFavoritos.setChecked(true);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContenedorFragments, new FragmentBuses())
                    .commit();
            ultimoChk = 1;
            rbBuses.setChecked(true);
            rbFavoritos.setVisibility(View.GONE);
        }
        radioGroup.setOnCheckedChangeListener(checkedChangeListenerPestanyas);
        String hint = getString(R.string.numero_poste_hint);
        SpannableString spannableString = new SpannableString(hint);
        spannableString.setSpan(new RelativeSizeSpan(0.6f), 0, hint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Establecer el texto con tamaño relativo como hint
        edtNPoste.setHint(spannableString);
    }

    private Boolean comprobarExpresionRegular(String cadenaAComparar, String expresionRegular) {
        Pattern pat = Pattern.compile(expresionRegular);
        Matcher mat = pat.matcher(cadenaAComparar);
        return mat.matches();
    }

    private void inicializarVistas() {
        radioGroup = findViewById(R.id.rgPestanyas);
        rbBuses = findViewById(R.id.rbBuses);
        rbFavoritos = findViewById(R.id.rbFavoritos);
        edtNPoste = findViewById(R.id.edtNumeroPoste);
        txtNombrePoste = findViewById(R.id.txtNombrePoste);
        sldNoticias = findViewById(R.id.sldNoticias);

        // Temperatura
        clTiempo = findViewById(R.id.clTiempo);
        txtTempActual = findViewById(R.id.txtTemperaturaActual);
        txtTempMin = findViewById(R.id.txtTemperaturaMinima);
        txtTempMax = findViewById(R.id.txtTemperaturaMaxima);
        txtEstadoCielo = findViewById(R.id.txtEstadoCielo);
    }

    private void inicializarEventos() {

        textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence numPosteCadena, int start, int before, int count) {
                txtNombrePoste.setText("");
                if (edtNPoste.getText().toString().isEmpty()) {
                    radioGroup.setOnCheckedChangeListener(null);
                    if (ultimoChk == 1) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContenedorFragments, new FragmentBuses())
                                .commit();
                        rbBuses.setChecked(true);
                    } else if (ultimoChk == 2) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContenedorFragments, new FragmentFavoritos())
                                .commit();
                        rbFavoritos.setChecked(true);
                    }
                    radioGroup.setOnCheckedChangeListener(checkedChangeListenerPestanyas);
                } else {
                    handlerCargaPoste.removeCallbacksAndMessages(null);
                    handlerCargaPoste.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("pruebaHand", "PRUEBA Handler");
                            iniciaCargaPoste(numPosteCadena);
                        }
                    }, 800);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edtNPoste.addTextChangedListener(textChangedListener);


        final Observer<String> mensajeErrorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String nuevoMensajeError) {
                Toast toast = Toast.makeText(MainActivity.this, nuevoMensajeError, Toast.LENGTH_LONG);
                toast.show();
            }
        };

        checkedChangeListenerPestanyas = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbBuses.getId() == checkedId) {
                    if (rbBuses.isChecked()) {
                        getSupportFragmentManager().popBackStack();
                        ultimoChk = 1;
                        edtNPoste.setText("");
                    }
                } else if (rbFavoritos.getId() == checkedId) {
                    if (rbFavoritos.isChecked()) {
                        getSupportFragmentManager().popBackStack();
                        ultimoChk = 2;
                        edtNPoste.setText("");
                    }
                }
            }
        };

    }

    private void iniciaCargaPoste(CharSequence numPosteCadena) {
        if (!numPosteCadena.toString().isEmpty() && numPosteCadena != null) {
            int numPosteInt = Integer.parseInt(String.valueOf(numPosteCadena));
            String numPoste = String.valueOf(numPosteInt);
            FragmentTiemposPoste fragmentParada = FragmentTiemposPoste.newInstance(numPoste);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContenedorFragments, fragmentParada)
                    .commit();
        }
    }


    public void insertarBusesEnBD(List<Buses> listaBuses) {
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        BusesDao daoLineaDeBus = db.daoBus();

        for (Buses bus : listaBuses) {
            daoLineaDeBus.insertarBus(bus);
        }
    }

    public void insertarPostesEnBD(List<Postes> listaPostes) {
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        PostesDao daoPoste = db.daoPoste();

        for (Postes poste : listaPostes) {
            daoPoste.insertarPoste(poste);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        db.close();
    }

    @Override
    public void onBackPressed() {
        if (!edtNPoste.getText().toString().equals("")) {
            edtNPoste.setText("");
        } else {
            super.onBackPressed();
            if (ultimoChk == 1) {
                rbBuses.setChecked(true);
            } else if (ultimoChk == 2) {
                rbFavoritos.setChecked(true);
            }
        }
    }

}
