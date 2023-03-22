package abeljs.xzaragoza.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.MainActivity;
import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.DaoLineaDeBus;
import abeljs.xzaragoza.data.LineaDeBus;


public class FragmentLineasDeBuses extends Fragment implements LineaSelectedInterface {

    RecyclerView rvLineasDeBus;
    List<LineaDeBus> listaLineasDeBuses;

    public FragmentLineasDeBuses() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_lineas_de_buses_list, container, false);

        rvLineasDeBus = vista.findViewById(R.id.rvLista);
        rvLineasDeBus.setLayoutManager(new LinearLayoutManager(getContext()));

        BaseDeDatos db = Room.databaseBuilder(getActivity().getApplicationContext(),
                BaseDeDatos.class, "Buses").allowMainThreadQueries().build();
        DaoLineaDeBus daoLineaDeBus = db.daoLineaDeBus();

        listaLineasDeBuses = daoLineaDeBus.getAllLineasDeBus();

        LineasDeBusesAdapter adaptadorLineas = new LineasDeBusesAdapter(listaLineasDeBuses, this);
        rvLineasDeBus.setAdapter(adaptadorLineas);

        return  vista;
    }

    @Override
    public void onLineaSelected(LineaDeBus selectedlineaDeBus) {

        MainActivity activity = (MainActivity) getActivity();
        activity.remplazarPorFragmentDirecciones(selectedlineaDeBus.numLinea, selectedlineaDeBus.direccion1, selectedlineaDeBus.direccion2);
    }
}