package abeljs.xzaragoza.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.LineaDeBus;


public class LineasDeBusesAdapter extends RecyclerView.Adapter<LineasDeBusesAdapter.ViewHolder> {

    private List<LineaDeBus> listaLineas;
    private final LineaSelectedInterface lineaSelected;

    public LineasDeBusesAdapter(List<LineaDeBus> listaLineas, LineaSelectedInterface lineaSelected) {
        this.listaLineas = listaLineas;
        this.lineaSelected = lineaSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_linea_bus,parent,false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LineaDeBus lineaDeBus = listaLineas.get(position);

        holder.txtNumLinea.setText(lineaDeBus.numLinea);
        holder.txtRecorrido.setText(lineaDeBus.direccion1);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineaSelected.onLineaSelected(lineaDeBus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaLineas.size();
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