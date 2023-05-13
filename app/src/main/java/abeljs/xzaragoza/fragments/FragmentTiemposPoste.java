package abeljs.xzaragoza.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.TiemposPosteAdapter;
import abeljs.xzaragoza.apis.BusquedaTiemposPosteAPI;
import abeljs.xzaragoza.apis.BusquedaTiemposPosteCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.PostesDao;
import abeljs.xzaragoza.data.TiempoBus;


public class FragmentTiemposPoste extends Fragment {

    private static final String NUM_POSTE = "numPoste";
    private static final String SE_HA_ESCRITO = "seHaEscrito";
    private static final int TIEMPO = 30000;

    private String numPoste;
    private Boolean seHaEscrito;
    private TiemposPosteAdapter adaptadorTiemposBuses;
    private Handler handler = new Handler();


    TextView txtNombrePoste;
    CheckBox chkFavorito;
    RecyclerView rvTiemposPoste;
    SwipeRefreshLayout srlRecargar;
    ArrayList<TiempoBus> listaTiemposBuses = new ArrayList<>();


    public FragmentTiemposPoste() {
    }

    public static FragmentTiemposPoste newInstance(String numPoste, boolean seHaEscrito) {
        FragmentTiemposPoste fragment = new FragmentTiemposPoste();
        Bundle args = new Bundle();
        args.putString(NUM_POSTE, numPoste);
        args.putBoolean(SE_HA_ESCRITO, seHaEscrito);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numPoste = getArguments().getString(NUM_POSTE);
            seHaEscrito = getArguments().getBoolean(SE_HA_ESCRITO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_tiempos_poste, container, false);
        txtNombrePoste = getActivity().findViewById(R.id.txtNombrePoste);
        rvTiemposPoste = vista.findViewById(R.id.rvTiemposPoste);
        rvTiemposPoste.setLayoutManager(new LinearLayoutManager(getContext()));
        srlRecargar = vista.findViewById(R.id.srlRecargarLayout);
        chkFavorito = getActivity().findViewById(R.id.chkFavorito);
        chkFavorito.setVisibility(View.VISIBLE);

        srlRecargar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.removeCallbacksAndMessages(null);
                recargaDatos();
            }
        });

        adaptadorTiemposBuses = new TiemposPosteAdapter(listaTiemposBuses);
        rvTiemposPoste.setAdapter(adaptadorTiemposBuses);

        return vista;
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
        BusquedaTiemposPosteAPI api = new BusquedaTiemposPosteAPI();
        api.getTiemposPoste(new BusquedaTiemposPosteCallback() {
            @Override
            public void onBusquedaTiemposPosteComplete(ArrayList<TiempoBus> result) {
                listaTiemposBuses.clear();
                for (int contador = 0; contador < result.size(); contador++) {
                    listaTiemposBuses.add(result.get(contador));
                }

                rvTiemposPoste.post(new Runnable() {
                    @Override
                    public void run() {
                        adaptadorTiemposBuses.notifyDataSetChanged();
                        if (!listaTiemposBuses.isEmpty()) {
                            BaseDeDatos db = Room.databaseBuilder(getActivity(),
                                    BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
                            PostesDao daoPoste = db.daoPoste();
                            String nombre = daoPoste.getPoste(numPoste).get(0).nombrePoste.trim();
                            txtNombrePoste.setText(nombre);
                        }
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
            public void onBusquedaTiemposPosteError(String cadenaError) {
                Log.e("prueba", "error");
            }
        }, numPoste);
    }


}