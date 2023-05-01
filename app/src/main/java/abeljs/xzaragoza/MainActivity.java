package abeljs.xzaragoza;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.apis.BusquedaLineasDeBusesAPI;
import abeljs.xzaragoza.apis.BusquedaLineasDeBusesCallback;
import abeljs.xzaragoza.apis.BusquedaPosteAPI;
import abeljs.xzaragoza.apis.BusquedaPosteCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.DaoBuses;
import abeljs.xzaragoza.data.TiempoBus;
import abeljs.xzaragoza.fragments.FragmentLineasDeBuses;
import abeljs.xzaragoza.fragments.FragmentParada;

public class MainActivity extends AppCompatActivity {

    EditText edtNPoste;

    private MainActivityViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVistas();
        inicializarEventos();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContenedorFragments, new FragmentLineasDeBuses())
                .commit();

    }

    private void inicializarVistas(){
        edtNPoste = findViewById(R.id.edtNumeroPoste);
    }

    private void inicializarEventos() {

        edtNPoste.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String numPoste = String.valueOf(s);

                BusquedaPosteAPI api = new BusquedaPosteAPI();
                api.buscarPoste(new BusquedaPosteCallback() {
                    @Override
                    public void onBusquedaPosteComplete(ArrayList<TiempoBus> result) {
                        ArrayList<TiempoBus> listaParadasLineaDeBus = result;
                        FragmentParada fragmentParada = FragmentParada.newInstance(listaParadasLineaDeBus);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContenedorFragments, fragmentParada)
                                .commit();
                    }

                    @Override
                    public void onBusquedaPosteError(String cadenaError) {
                        Log.e("prueba", "error");
                    }
                }, numPoste);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        final Observer<String> mensajeErrorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String nuevoMensajeError) {
                Toast toast = Toast.makeText(MainActivity.this, nuevoMensajeError, Toast.LENGTH_LONG);
                toast.show();
            }
        };

        model.getMensajeError().observe(MainActivity.this, mensajeErrorObserver);

        BusquedaLineasDeBusesAPI api = new BusquedaLineasDeBusesAPI();
        api.getLineasBuses(new BusquedaLineasDeBusesCallback() {
            @Override
            public void onBusquedaLineasDeBusesComplete(List<Buses> listaLineasDeBus) {
                insertarLineasBusesEnBD(listaLineasDeBus);
            }

            @Override
            public void onBusquedaLineasDeBusesError(String cadenaError) {
                model.getMensajeError().postValue(cadenaError);
            }
        });
    }


    public void insertarLineasBusesEnBD(List<Buses> listaLineasDeBus) {
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, "Buses").allowMainThreadQueries().build();
        DaoBuses daoLineaDeBus = db.daoLineaDeBus();

        for (Buses lineaDeBus : listaLineasDeBus) {
            daoLineaDeBus.insertarLineasDeBus(lineaDeBus);

        }

    }
}
