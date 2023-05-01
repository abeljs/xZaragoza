package abeljs.xzaragoza.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.LineasDeBusesAdapter;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.DaoBuses;


public class FragmentLineasDeBuses extends Fragment implements LineaSelectedInterface {

    RecyclerView rvLineasDeBus;
    List<Buses> listaLineasDeBuses;

    public FragmentLineasDeBuses() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_lineas_de_buses_list, container, false);

        rvLineasDeBus = vista.findViewById(R.id.rvListaLineasBuses);
        rvLineasDeBus.setLayoutManager(new LinearLayoutManager(getContext()));

        BaseDeDatos db = Room.databaseBuilder(getActivity().getApplicationContext(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        DaoBuses daoLineaDeBus = db.daoLineaDeBus();

        listaLineasDeBuses = daoLineaDeBus.getAllLineasDeBus();

        LineasDeBusesAdapter adaptadorLineas = new LineasDeBusesAdapter(listaLineasDeBuses, this);
        rvLineasDeBus.setAdapter(adaptadorLineas);

        return  vista;
    }

    @Override
    public void onLineaSelected(Buses selectedlineaDeBus) {
        Fragment fragment = FragmentDireccionesLineas.newInstance
                        (selectedlineaDeBus.numBus,
                        selectedlineaDeBus.direccion1,
                        selectedlineaDeBus.direccion2);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContenedorFragments, fragment)
                .addToBackStack(null)
                .commit();
    }
}