package abeljs.xzaragoza.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.MainActivity;
import abeljs.xzaragoza.R;
import abeljs.xzaragoza.adaptadores.FavoritosAdapter;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Favoritos;
import abeljs.xzaragoza.data.FavoritosDao;


public class FragmentFavoritos extends Fragment implements FavoritoSelectedInterface {

    CheckBox chkFavorito;
    List<Favoritos> listaFavoritos;
    RecyclerView rvFavoritos;
    FavoritosAdapter adaptadorFavoritos;

    public FragmentFavoritos() {
    }


    public static FragmentFavoritos newInstance() {
        FragmentFavoritos fragment = new FragmentFavoritos();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_favoritos, container, false);

        rvFavoritos = vista.findViewById(R.id.rvFavoritos);
        chkFavorito = getActivity().findViewById(R.id.chkFavorito);
        chkFavorito.setEnabled(false);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(getActivity()));


        return vista;
    }

    @Override
    public void onFavoritoSelected(Favoritos selectedFavorito) {

        EditText editText = getActivity().findViewById(R.id.edtNumeroPoste);
        editText.setText(selectedFavorito.numPoste);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseDeDatos db = Room.databaseBuilder(getActivity().getApplicationContext(),
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        FavoritosDao favoritosDao = db.daoFavoritos();
        listaFavoritos = favoritosDao.getFavoritos();

        adaptadorFavoritos = new FavoritosAdapter(listaFavoritos, this);
        rvFavoritos.setAdapter(adaptadorFavoritos);    }
}