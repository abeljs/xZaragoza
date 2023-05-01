package abeljs.xzaragoza.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.LineasDeBusesDireccionesAdapter;


public class FragmentDireccionesLineas extends Fragment {

    private static final String NUM_LINEA = "numLinea";
    private static final String DIRECCION1 = "direccion1";
    private static final String DIRECCION2 = "direccion2";

    private RecyclerView rvDirecciones;
    private String numLinea,direccion1, direccion2;

    public FragmentDireccionesLineas() {
    }

    public static FragmentDireccionesLineas newInstance(String numLinea, String direccion1, String direccion2) {
        FragmentDireccionesLineas fragment = new FragmentDireccionesLineas();
        Bundle args = new Bundle();
        args.putString(NUM_LINEA, numLinea);
        args.putString(DIRECCION1, direccion1);
        args.putString(DIRECCION2, direccion2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numLinea = getArguments().getString(NUM_LINEA);
            direccion1 = getArguments().getString(DIRECCION1);
            direccion2 = getArguments().getString(DIRECCION2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_lineas_de_buses_list, container, false);

        rvDirecciones = vista.findViewById(R.id.rvListaLineasBuses);
        rvDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> listaDirecciones = new ArrayList<>();
        listaDirecciones.add(direccion1);
        listaDirecciones.add(direccion2);
        LineasDeBusesDireccionesAdapter adaptadorDirecciones = new LineasDeBusesDireccionesAdapter(numLinea, listaDirecciones);
        rvDirecciones.setAdapter(adaptadorDirecciones);

        return  vista;
    }

}