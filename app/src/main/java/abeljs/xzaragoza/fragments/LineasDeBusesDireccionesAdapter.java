package abeljs.xzaragoza.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;


public class LineasDeBusesDireccionesAdapter extends RecyclerView.Adapter<LineasDeBusesDireccionesAdapter.ViewHolder> {

    private final String numLinea;
    private final List<String> listaDirecciones;

    public LineasDeBusesDireccionesAdapter(String numLinea, List<String> listaDirecciones) {
        this.numLinea = numLinea;
        this.listaDirecciones = listaDirecciones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_linea_bus,parent,false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtNumLinea.setText(numLinea);
        holder.txtRecorrido.setText(listaDirecciones.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDirecciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumLinea, txtRecorrido;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumLinea = itemView.findViewById(R.id.txtNumeroLinea);
            txtRecorrido = itemView.findViewById(R.id.txtRecorrido);
        }

    }
}