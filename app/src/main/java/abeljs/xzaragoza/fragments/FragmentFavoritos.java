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

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.MainActivity;
import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.BusPostesAdapter;
import abeljs.xzaragoza.adaptadores.FavoritosAdapter;
import abeljs.xzaragoza.apis.BusquedaBusPostesAPI;
import abeljs.xzaragoza.apis.BusquedaBusPostesCallback;
import abeljs.xzaragoza.apis.BusquedaTiemposPosteCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.BusPostes;
import abeljs.xzaragoza.data.BusPostesDao;
import abeljs.xzaragoza.data.Favoritos;
import abeljs.xzaragoza.data.FavoritosDao;
import abeljs.xzaragoza.data.TiempoBus;


public class FragmentFavoritos extends Fragment implements FavoritoSelectedInterface{

    private List<Favoritos> listaFavoritos;
    private RecyclerView rvFavoritos;


    public FragmentFavoritos() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_favoritos, container, false);


        BaseDeDatos db = Room.databaseBuilder(getActivity(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        FavoritosDao daoFavoritos = db.daoFavoritos();

        rvFavoritos = vista.findViewById(R.id.rvFavoritos);

        listaFavoritos = daoFavoritos.getFavoritos();

        FavoritosAdapter adaptadorFavoritos = new FavoritosAdapter(listaFavoritos, this);
        rvFavoritos.setAdapter(adaptadorFavoritos);

        return  vista;
    }

    @Override
    public void onFavoritoSelected(Favoritos selectedFavorito) {
        ((MainActivity) getActivity()).seHaEscrito = false;

        EditText editText = getActivity().findViewById(R.id.edtNumeroPoste);
        editText.setText(selectedFavorito.numPoste);
    }
}