package abeljs.xzaragoza;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.apis.BusquedaBusesAPI;
import abeljs.xzaragoza.apis.BusquedaBusesCallback;
import abeljs.xzaragoza.apis.BusquedaPostesAPI;
import abeljs.xzaragoza.apis.BusquedaPostesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;
import abeljs.xzaragoza.data.Favoritos;
import abeljs.xzaragoza.data.FavoritosDao;
import abeljs.xzaragoza.data.Postes;
import abeljs.xzaragoza.data.PostesDao;
import abeljs.xzaragoza.fragments.FragmentBuses;
import abeljs.xzaragoza.fragments.FragmentFavoritos;
import abeljs.xzaragoza.fragments.FragmentTiemposPoste;

public class MainActivity extends AppCompatActivity {

    Context contexto;
    EditText edtNPoste;
    TextView txtNombrePoste;
    RadioGroup radioGroup;
    RadioButton rbBuses;
    RadioButton rbFavoritos;

    private MainActivityViewModel model;
    private TextWatcher textChangedListener;
    private int ultimoChk = 1; // 1 = Buses, 2 = Favoritos
    private RadioGroup.OnCheckedChangeListener checkedChangeListenerPestanyas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        contexto = this;

        inicializarVistas();
        inicializarEventos();

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

    private void inicializarVistas() {
        radioGroup = findViewById(R.id.rgPestanyas);
        rbBuses = findViewById(R.id.rbBuses);
        rbFavoritos = findViewById(R.id.rbFavoritos);
        edtNPoste = findViewById(R.id.edtNumeroPoste);
        txtNombrePoste = findViewById(R.id.txtNombrePoste);
    }

    private void inicializarEventos() {

        textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    String numPoste = String.valueOf(s);
                    FragmentTiemposPoste fragmentParada = FragmentTiemposPoste.newInstance(numPoste);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContenedorFragments, fragmentParada)
                            .commit();

                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edtNPoste.addTextChangedListener(textChangedListener);

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        final Observer<String> mensajeErrorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String nuevoMensajeError) {
                Toast toast = Toast.makeText(MainActivity.this, nuevoMensajeError, Toast.LENGTH_LONG);
                toast.show();
            }
        };

        model.getMensajeError().observe(MainActivity.this, mensajeErrorObserver);

        BusquedaBusesAPI apiBusquedaBuses = new BusquedaBusesAPI();
        apiBusquedaBuses.getBuses(new BusquedaBusesCallback() {
            @Override
            public void onBusquedaLineasDeBusesComplete(List<Buses> listaLineasDeBus) {
                insertarBusesEnBD(listaLineasDeBus);

//                for (Buses bus : listaLineasDeBus) {
//                    BusquedaBusPostesAPI apiBusPostes = new BusquedaBusPostesAPI();
//                    apiBusPostes.getBusPostes(new BusquedaBusPostesCallback() {
//                        @Override
//                        public void onBusquedaBusPostesComplete(List<BusPostes> listaBusPostes) {
//                            if (listaBusPostes != null) {
//                                if (listaBusPostes.size() > 0) {
//                                    Log.e("pruebaDef", listaBusPostes.get(0).destino + " "
//                                            + listaBusPostes.get(0).numBus + " "
//                                            + listaBusPostes.get(0).numPoste + " "
//                                            + listaBusPostes.get(0).orden);
//                                    insertarBusPostesEnBD(listaBusPostes);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onBusquedaBusPostesError(String cadenaError) {
//
//                        }
//                    }, bus.numBus);
//                }
            }

            @Override
            public void onBusquedaLineasDeBusesError(String cadenaError) {
                model.getMensajeError().postValue(cadenaError);
            }
        });

        BusquedaPostesAPI apiBusquedaPostes = new BusquedaPostesAPI();
        apiBusquedaPostes.getPostes(new BusquedaPostesCallback() {
            @Override
            public void onBusquedaPostesComplete(ArrayList<Postes> result) {
                insertarPostesEnBD(result);
            }

            @Override
            public void onBusquedaPostesError(String cadenaError) {
                model.getMensajeError().postValue(cadenaError);
            }

        });


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
    public void onBackPressed() {
        if (!edtNPoste.getText().toString().equals("")){
            edtNPoste.setText("");
        } else {
            super.onBackPressed();
            if(ultimoChk == 1) {
                rbBuses.setChecked(true);
            } else if (ultimoChk == 2) {
                rbFavoritos.setChecked(true);
            }
        }
    }
}
