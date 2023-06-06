package abeljs.xzaragoza.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.ZgzBusIntents;
import abeljs.xzaragoza.adaptadores.BusesAdapter;
import abeljs.xzaragoza.apis.BusquedaBusesAPI;
import abeljs.xzaragoza.apis.BusquedaBusesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;
import abeljs.xzaragoza.servicios.CargaBusesService;


public class FragmentBuses extends Fragment implements LineaSelectedInterface {

    private BusesAdapter adaptadorBuses;

    CheckBox chkFavorito;
    RecyclerView rvBuses;
    List<Buses> listaBuses;

    private BroadcastReceiver cargaBusesOkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("pruebaServicio", "onReceive");
            recargarDatos();
        }
    };

    private BroadcastReceiver cargaBusesErrorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra(ZgzBusIntents.MENSAJE_ERROR), Toast.LENGTH_SHORT).show();
        }
    };

    public FragmentBuses() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_buses, container, false);
        getActivity().getSupportFragmentManager().popBackStack();

        chkFavorito = getActivity().findViewById(R.id.chkFavorito);
        chkFavorito.setEnabled(false);
        rvBuses = vista.findViewById(R.id.rvBuses);
        rvBuses.setLayoutManager(new LinearLayoutManager(getContext()));

        BaseDeDatos db = Room.databaseBuilder(getActivity().getApplicationContext(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        BusesDao daoLineaDeBus = db.daoBus();

        listaBuses = daoLineaDeBus.getAllBuses();
        if (listaBuses.isEmpty()) {
            getActivity().startService(new Intent(getContext(), CargaBusesService.class));
        }

        adaptadorBuses = new BusesAdapter(listaBuses, this);
        rvBuses.setAdapter(adaptadorBuses);

        Log.e("pruebaServicio", "onCreateView");

        return vista;
    }


    private void recargarDatos(){
        BaseDeDatos db = Room.databaseBuilder(getActivity().getApplicationContext(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        BusesDao daoLineaDeBus = db.daoBus();

        listaBuses.clear();
        listaBuses.addAll(daoLineaDeBus.getAllBuses());

        Log.e("pruebaServicio", "recargaDatos");

        adaptadorBuses.notifyDataSetChanged();
    }

    @Override
    public void onLineaSelected(Buses selectedlineaDeBus) {
        Fragment fragment = FragmentDireccionesBuses.newInstance
                        (selectedlineaDeBus.numBus,
                        selectedlineaDeBus.direccion1,
                        selectedlineaDeBus.direccion2);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContenedorFragments, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());

        broadcastManager.registerReceiver(cargaBusesOkBroadcastReceiver, new IntentFilter(ZgzBusIntents.BUSES_CARGADOS_OK));
        broadcastManager.registerReceiver(cargaBusesErrorBroadcastReceiver, new IntentFilter(ZgzBusIntents.BUSES_CARGADOS_ERROR));

    }

    @Override
    public void onPause (){
        super.onPause();
        Log.e("pruebaServicio", "onPause");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());

        broadcastManager.registerReceiver(cargaBusesOkBroadcastReceiver, new IntentFilter(ZgzBusIntents.BUSES_CARGADOS_OK));
        broadcastManager.registerReceiver(cargaBusesErrorBroadcastReceiver, new IntentFilter(ZgzBusIntents.BUSES_CARGADOS_ERROR));
    }

}