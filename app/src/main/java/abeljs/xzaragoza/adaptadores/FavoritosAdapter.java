package abeljs.xzaragoza.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.Favoritos;
import abeljs.xzaragoza.fragments.FavoritoSelectedInterface;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private List<Favoritos> listaFavoritos;
    private FavoritoSelectedInterface favoritoSelected;

    public FavoritosAdapter(List<Favoritos> listaFavoritos, FavoritoSelectedInterface favoritoSelectedInterface) {
        this.listaFavoritos = listaFavoritos;
        this.favoritoSelected = favoritoSelectedInterface;
    }

    @Override
    public FavoritosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_bus,parent,false);
        return new FavoritosAdapter.ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(FavoritosAdapter.ViewHolder holder, int position) {
        Favoritos favorito = listaFavoritos.get(position);

        holder.txtNombrePoste.setText(favorito.nombreFavorito);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritoSelected.onFavoritoSelected(favorito);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFavoritos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombrePoste;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombrePoste = itemView.findViewById(R.id.txtNombrePosteFavorito);
        }

    }
}
