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

import abeljs.xzaragoza.adaptadores.SliderNoticiasAdapter;
import abeljs.xzaragoza.data.BaseDeDatos;
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
import abeljs.xzaragoza.servicios.CargaNoticiasService;
import abeljs.xzaragoza.servicios.CargaPostesService;
import abeljs.xzaragoza.servicios.CargaTiempoService;

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
    ElTiempoView tiempoView;
    List<Postes> lstPostes;

    private SliderNoticiasAdapter sliderNoticiasAdapter;
    private TextWatcher textChangedListener;
    private int ultimoChk = 1; // 1 = Buses, 2 = Favoritos
    private RadioGroup.OnCheckedChangeListener checkedChangeListenerPestanyas;
    private Handler handlerCargaPoste = new Handler();

    private BroadcastReceiver cargaTiempoOkBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tiempoView.setTemperatura((Temperatura) intent.getSerializableExtra(ZgzBusIntents.EL_TIEMPO_RESULTADO));
        }
    };

    private BroadcastReceiver cargaTiempoErrorBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tiempoView.setDefault();
        }
    };


    private BroadcastReceiver cargaPostesOkBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private BroadcastReceiver cargaPostesErrorBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra(ZgzBusIntents.MENSAJE_ERROR), Toast.LENGTH_SHORT).show();
        }
    };

    private BroadcastReceiver cargaNoticiasOkBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("pruebaNoticias", "broadcast");
            Calendar fechaBorrado = Calendar.getInstance();
            fechaBorrado.add(Calendar.DATE, DIAS_CON_NOTICIA);

            BaseDeDatos db = Room.databaseBuilder(getApplicationContext(),
                    BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
            NoticiasDao daoNoticias = db.daoNoticias();

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
    };

    private BroadcastReceiver cargaNoticiasErrorBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, CargaNoticiasService.class));
        BaseDeDatos db = Room.databaseBuilder(getApplicationContext(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        PostesDao daoPostes = db.daoPostes();

        lstPostes = daoPostes.getPostes();
        if (lstPostes.isEmpty()) {
            startService(new Intent(getApplicationContext(), CargaPostesService.class));
        }

        contexto = this;

        inicializarVistas();
        inicializarEventos();


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

    @Override
    protected void onResume() {

        super.onResume();
        startService(new Intent(this, CargaTiempoService.class));
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.registerReceiver(cargaTiempoOkBroadcastReceiver, new IntentFilter(ZgzBusIntents.EL_TIEMPO_CARGADO_OK));
        broadcastManager.registerReceiver(cargaTiempoErrorBroadcastReceiver, new IntentFilter(ZgzBusIntents.EL_TIEMPO_CARGADO_ERROR));

        broadcastManager.registerReceiver(cargaPostesOkBroadcastReceiver, new IntentFilter(ZgzBusIntents.POSTES_CARGADOS_OK));
        broadcastManager.registerReceiver(cargaPostesErrorBroadcastReceiver, new IntentFilter(ZgzBusIntents.POSTES_CARGADOS_ERROR));

        broadcastManager.registerReceiver(cargaNoticiasOkBroadcastReceiver, new IntentFilter(ZgzBusIntents.NOTICIAS_CARGADAS_OK));
        broadcastManager.registerReceiver(cargaNoticiasErrorBroadcastReceiver, new IntentFilter(ZgzBusIntents.NOTICIAS_CARGADAS_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(cargaTiempoOkBroadcastReceiver);
        broadcastManager.unregisterReceiver(cargaTiempoErrorBroadcastReceiver);

        broadcastManager.unregisterReceiver(cargaPostesOkBroadcastReceiver);
        broadcastManager.unregisterReceiver(cargaPostesErrorBroadcastReceiver);

        broadcastManager.unregisterReceiver(cargaNoticiasOkBroadcastReceiver);
        broadcastManager.unregisterReceiver(cargaNoticiasErrorBroadcastReceiver);
    }


    private void inicializarVistas() {
        radioGroup = findViewById(R.id.rgPestanyas);
        rbBuses = findViewById(R.id.rbBuses);
        rbFavoritos = findViewById(R.id.rbFavoritos);
        edtNPoste = findViewById(R.id.edtNumeroPoste);
        txtNombrePoste = findViewById(R.id.txtNombrePoste);
        sldNoticias = findViewById(R.id.sldNoticias);

        // Temperatura
        tiempoView = findViewById(R.id.tvElTiempo);
        clTiempo = findViewById(R.id.clTiempo);
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
                        getSupportFragmentManager().popBackStack();
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
