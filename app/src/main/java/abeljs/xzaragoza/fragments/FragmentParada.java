package abeljs.xzaragoza.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.TiemposBusesAdapter;
import abeljs.xzaragoza.data.TiempoBus;


public class FragmentParada extends Fragment {

    private static final String BUSES = "buses";

    RecyclerView rvLineasDeBus;
    ArrayList<TiempoBus> listaBuses;

    public FragmentParada() {
    }

    public static FragmentParada newInstance(ArrayList<TiempoBus> listaBuses) {
        FragmentParada fragment = new FragmentParada();
        Bundle args = new Bundle();
        args.putSerializable(BUSES, listaBuses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listaBuses = (ArrayList<TiempoBus>) getArguments().getSerializable(BUSES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_parada, container, false);

        rvLineasDeBus = vista.findViewById(R.id.rvListaLineasParada);
        rvLineasDeBus.setLayoutManager(new LinearLayoutManager(getContext()));
        TiemposBusesAdapter adaptadorTiemposBuses = new TiemposBusesAdapter(listaBuses);
        rvLineasDeBus.setAdapter(adaptadorTiemposBuses);

        return  vista;
    }
}