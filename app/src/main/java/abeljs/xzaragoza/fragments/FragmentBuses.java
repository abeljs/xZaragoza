package abeljs.xzaragoza.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.BusesAdapter;
import abeljs.xzaragoza.apis.BusquedaBusesAPI;
import abeljs.xzaragoza.apis.BusquedaBusesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;


public class FragmentBuses extends Fragment implements LineaSelectedInterface {

    private BusesAdapter adaptadorBuses;

    CheckBox chkFavorito;
    RecyclerView rvBuses;
    List<Buses> listaBuses;

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

        adaptadorBuses = new BusesAdapter(listaBuses, this);
        rvBuses.setAdapter(adaptadorBuses);

        return  vista;
    }


    private void recargarDatos(){
        BusquedaBusesAPI api = new BusquedaBusesAPI();
        api.getBuses(new BusquedaBusesCallback() {
            @Override
            public void onBusquedaLineasDeBusesComplete(List<Buses> result) {
                listaBuses.clear();
                for (int contador = 0 ; contador < result.size() ; contador++){
                    listaBuses.add(result.get(contador));
                }

                rvBuses.post(new Runnable() {
                    @Override
                    public void run() {
                        adaptadorBuses.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onBusquedaLineasDeBusesError(String cadenaError) {
                Log.e("prueba", "error");
            }
        });
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
}