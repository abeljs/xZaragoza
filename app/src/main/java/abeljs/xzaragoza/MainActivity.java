package abeljs.xzaragoza;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import abeljs.xzaragoza.apis.BusquedaLineasDeBusesAPI;
import abeljs.xzaragoza.apis.BusquedaLineasDeBusesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.DaoLineaDeBus;
import abeljs.xzaragoza.data.LineaDeBus;
import abeljs.xzaragoza.fragments.FragmentDireccionesLineas;
import abeljs.xzaragoza.fragments.FragmentLineasDeBuses;
import abeljs.xzaragoza.fragments.LineaSelectedInterface;
import abeljs.xzaragoza.fragments.LineasDeBusesDireccionesAdapter;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fTransaccion;
    Fragment fLineasDeBus;
    private MainActivityViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fLineasDeBus = new FragmentLineasDeBuses();

        getSupportFragmentManager().beginTransaction().add(R.id.flContenedorFragments, fLineasDeBus).commit();

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
            public void onBusquedaLineasDeBusesComplete(List<LineaDeBus> listaLineasDeBus) {
                insertarLineasBusesEnBD(listaLineasDeBus);
            }

            @Override
            public void onBusquedaLineasDeBusesError(String cadenaError) {
                model.getMensajeError().postValue(cadenaError);

            }
        });

    }


    public void insertarLineasBusesEnBD(List<LineaDeBus> listaLineasDeBus) {
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, "Buses").allowMainThreadQueries().build();
        DaoLineaDeBus daoLineaDeBus = db.daoLineaDeBus();

        for (LineaDeBus lineaDeBus : listaLineasDeBus) {
            daoLineaDeBus.insertarLineasDeBus(lineaDeBus);
        }

    }

    public void remplazarPorFragmentDirecciones(String numLinea, String direccion1, String direccion2){
        Fragment fragment = FragmentDireccionesLineas.newInstance
                    (numLinea, direccion1, direccion2);

        fTransaccion = getSupportFragmentManager().beginTransaction();
        fTransaccion.replace(R.id.flContenedorFragments, fragment);
        fTransaccion.addToBackStack(null);
        fTransaccion.commit();
    }
}
