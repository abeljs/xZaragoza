package abeljs.xzaragoza.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.MainActivity;
import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.BusPostesAdapter;
import abeljs.xzaragoza.apis.BusquedaBusPostesAPI;
import abeljs.xzaragoza.apis.BusquedaBusPostesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.BusPostes;
import abeljs.xzaragoza.data.BusPostesDao;


public class FragmentDireccionesBuses extends Fragment implements BusPostesSelectedInterface {

    private static final String NUM_LINEA = "numLinea";
    private static final String DIRECCION1 = "direccion1";
    private static final String DIRECCION2 = "direccion2";


    private String numBus, direccion1, direccion2;

    private BaseDeDatos db;
    private BusPostesDao daoBusPostes;

    private RecyclerView rvDirecciones;

    private List<BusPostes> listaBusPostes = new ArrayList<>();
    private BusPostesAdapter adaptadorBusPostes;
    private CheckBox chkFavorito;
    private RadioGroup rgPestanyas;
    private RadioGroup rgDirecciones;
    private RadioButton rbDireccion1;
    private RadioButton rbDireccion2;

    public FragmentDireccionesBuses() {
    }

    public static FragmentDireccionesBuses newInstance(String numLinea, String direccion1, String direccion2) {
        FragmentDireccionesBuses fragment = new FragmentDireccionesBuses();
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
            numBus = getArguments().getString(NUM_LINEA);
            direccion1 = getArguments().getString(DIRECCION1).split("-")[0].trim();
            direccion2 = getArguments().getString(DIRECCION2).split("-")[0].trim();
            db = Room.databaseBuilder(getActivity(),
                    BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
            daoBusPostes = db.daoBusPostes();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_direcciones_buses, container, false);

        BusPostesSelectedInterface busPostesSelectedInterface = this;

        chkFavorito = getActivity().findViewById(R.id.chkFavorito);

        chkFavorito.setEnabled(false);


        rgPestanyas = getActivity().findViewById(R.id.rgPestanyas);
        rgPestanyas.clearCheck();

        rgDirecciones = vista.findViewById(R.id.rgDirecciones);
        rbDireccion1 = vista.findViewById(R.id.rbDireccion1);
        rbDireccion2 = vista.findViewById(R.id.rbDireccion2);
        rbDireccion1.setChecked(true);
        rbDireccion1.setText(direccion1);
        rbDireccion2.setText(direccion2);

        rvDirecciones = vista.findViewById(R.id.rvDireccionesBuses);
        rvDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));

        listaBusPostes = daoBusPostes.getBusPostesPorDestinoBus(direccion1, numBus);

        adaptadorBusPostes = new BusPostesAdapter(getActivity(), busPostesSelectedInterface, listaBusPostes);
        rvDirecciones.setAdapter(adaptadorBusPostes);

        if (listaBusPostes.size() == 0 || listaBusPostes == null) {
            recargaDatos();
        }


        rgDirecciones.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbDireccion1.getId() == checkedId) {
                    if (rbDireccion1.isChecked()) {
                        listaBusPostes = daoBusPostes.getBusPostesPorDestinoBus(direccion1, numBus);
                        adaptadorBusPostes = new BusPostesAdapter(getActivity(), busPostesSelectedInterface, listaBusPostes);
                        rvDirecciones.setAdapter(adaptadorBusPostes);
                    }
                } else if (rbDireccion2.getId() == checkedId) {
                    if (rbDireccion2.isChecked()) {
                        listaBusPostes = daoBusPostes.getBusPostesPorDestinoBus(direccion2, numBus);
                        adaptadorBusPostes = new BusPostesAdapter(getActivity(), busPostesSelectedInterface, listaBusPostes);
                        rvDirecciones.setAdapter(adaptadorBusPostes);
                    }
                }
            }
        });


//        btnDireccion1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                btnDireccion2.setEnabled(false);
////                btnDireccion1.setEnabled(true);
//                listaBusPostes = daoBusPostes.getBusPostesPorDestinoBus(direccion1, numBus);
//
//                adaptadorBusPostes = new BusPostesAdapter(getActivity(), busPostesSelectedInterface, listaBusPostes);
//                rvDirecciones.setAdapter(adaptadorBusPostes);
//            }
//        });
//
//        btnDireccion2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                btnDireccion1.setEnabled(false);
////                btnDireccion2.setEnabled(true);
//                listaBusPostes = daoBusPostes.getBusPostesPorDestinoBus(direccion2, numBus);
//
//                adaptadorBusPostes = new BusPostesAdapter(getActivity(), busPostesSelectedInterface, listaBusPostes);
//                rvDirecciones.setAdapter(adaptadorBusPostes);
//            }
//        });

        return vista;
    }


//    public List<BusPostes> buscarPosteBus(String direccion) {
//        BaseDeDatos db = Room.databaseBuilder(getActivity(),
//                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
//        BusPostesDao daoBusPoste = db.daoBusPostes();
//
//        return daoBusPoste.getBusPostesPorDestinoBus(direccion, numBus);
//    }

    private void recargaDatos() {
        BusquedaBusPostesAPI api = new BusquedaBusPostesAPI();
        api.getBusPostes(new BusquedaBusPostesCallback() {
            @Override
            public void onBusquedaBusPostesComplete(List<BusPostes> result) {
                listaBusPostes.clear();

                for (int contador = 0; contador < result.size(); contador++) {
                    listaBusPostes.add(result.get(contador));
                    daoBusPostes.insertarBusPostes(result.get(contador));
                }

                rvDirecciones.post(new Runnable() {
                    @Override
                    public void run() {
                        adaptadorBusPostes.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onBusquedaBusPostesError(String cadenaError) {
                Log.e("prueba", "error");
            }
        }, numBus);
    }

    @Override
    public void onBusPostesSelected(BusPostes selectedBusPostes) {
//        FragmentTiemposPoste fragmentParada = FragmentTiemposPoste.newInstance(selectedBusPostes.numPoste);
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.flContenedorFragments, fragmentParada)
//                .addToBackStack(null)
//                .commit();


        EditText editText = getActivity().findViewById(R.id.edtNumeroPoste);
        editText.setText(selectedBusPostes.numPoste);
    }

}