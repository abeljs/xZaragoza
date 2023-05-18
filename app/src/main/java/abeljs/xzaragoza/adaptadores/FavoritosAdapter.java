package abeljs.xzaragoza.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.Favoritos;
import abeljs.xzaragoza.fragments.FavoritoSelectedInterface;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private List<Favoritos> listaFavoritos;
    private final FavoritoSelectedInterface favoritoSelected;

    public FavoritosAdapter(List<Favoritos> listaFavoritos, FavoritoSelectedInterface favoritoSelected) {
        this.listaFavoritos = listaFavoritos;
        this.favoritoSelected = favoritoSelected;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_poste, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favoritos favorito = listaFavoritos.get(position);

        holder.txtNombreFavorito.setText(favorito.nombreFavorito.trim());

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

    public class ViewHolder extends  RecyclerView.ViewHolder {
        TextView txtNombreFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreFavorito = itemView.findViewById(R.id.txtNombrePosteFavorito);
        }
    }
}
