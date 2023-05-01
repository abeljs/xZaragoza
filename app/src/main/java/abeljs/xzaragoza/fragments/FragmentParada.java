package abeljs.xzaragoza.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.TiemposBusesAdapter;
import abeljs.xzaragoza.apis.BusquedaPosteAPI;
import abeljs.xzaragoza.apis.BusquedaPosteCallback;
import abeljs.xzaragoza.data.TiempoBus;


public class FragmentParada extends Fragment {

    private static final String NUM_POSTE = "numPoste";
    private static final int TIEMPO = 30000;

    private String numPoste;
    private TiemposBusesAdapter adaptadorTiemposBuses;
    private Handler handler = new Handler();


    RecyclerView rvLineasDeBus;
    SwipeRefreshLayout srlRecargar;
    ArrayList<TiempoBus> listaBuses = new ArrayList<>();


    public FragmentParada() {
    }

    public static FragmentParada newInstance(String numPoste) {
        FragmentParada fragment = new FragmentParada();
        Bundle args = new Bundle();
        args.putString(NUM_POSTE, numPoste);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numPoste = getArguments().getString(NUM_POSTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_parada, container, false);
        rvLineasDeBus = vista.findViewById(R.id.rvListaLineasParada);
        rvLineasDeBus.setLayoutManager(new LinearLayoutManager(getContext()));
        srlRecargar = vista.findViewById(R.id.srlRecargarLayout);

        srlRecargar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.removeCallbacksAndMessages(null);
                recargaDatos();
            }
        });

        adaptadorTiemposBuses = new TiemposBusesAdapter(listaBuses);
        rvLineasDeBus.setAdapter(adaptadorTiemposBuses);

        return  vista;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacksAndMessages(null);
        recargaDatos();
    }

    private void recargaDatos() {
        BusquedaPosteAPI api = new BusquedaPosteAPI();
        api.buscarPoste(new BusquedaPosteCallback() {
            @Override
            public void onBusquedaPosteComplete(ArrayList<TiempoBus> result) {
                listaBuses.clear();
                for (int contador = 0 ; contador < result.size() ; contador++){
                    listaBuses.add(result.get(contador));
                }

                rvLineasDeBus.post(new Runnable() {
                    @Override
                    public void run() {
                        adaptadorTiemposBuses.notifyDataSetChanged();
                    }
                });

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recargaDatos();
                    }
                }, TIEMPO);

                srlRecargar.setRefreshing(false);
            }

            @Override
            public void onBusquedaPosteError(String cadenaError) {
                Log.e("prueba", "error");
            }
        }, numPoste);
    }

}