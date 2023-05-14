package abeljs.xzaragoza;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    public boolean seHaEscrito = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto = this;

        inicializarVistas();
        inicializarEventos();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContenedorFragments, new FragmentBuses())
                .commit();

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
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContenedorFragments, new FragmentBuses())
                            .commit();
                } else {
                    String numPoste = String.valueOf(s);
                    FragmentTiemposPoste fragmentParada = FragmentTiemposPoste.newInstance(numPoste, seHaEscrito);
                    seHaEscrito = true;
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


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbBuses.getId() == checkedId) {
                    edtNPoste.setText("");
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.flContenedorFragments, new FragmentBuses())
//                            .commit();
                } else if (rbFavoritos.getId() == checkedId) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContenedorFragments, new FragmentFavoritos())
                            .commit();
                }
            }
        });

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
        }
    }
}
