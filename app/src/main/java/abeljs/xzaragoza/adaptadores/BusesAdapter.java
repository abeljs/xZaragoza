package abeljs.xzaragoza.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.fragments.LineaSelectedInterface;


public class BusesAdapter extends RecyclerView.Adapter<BusesAdapter.ViewHolder> {

    private List<Buses> listaLineas;
    private final LineaSelectedInterface lineaSelected;

    public BusesAdapter(List<Buses> listaLineas, LineaSelectedInterface lineaSelected) {
        this.listaLineas = listaLineas;
        this.lineaSelected = lineaSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_bus,parent,false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Buses lineaDeBus = listaLineas.get(position);

        holder.txtNumLinea.setText(lineaDeBus.numBus);
        int colorResourceId = holder.itemView.getContext().getResources().getIdentifier("color_" + lineaDeBus.numBus, "color", holder.itemView.getContext().getPackageName());
        if (colorResourceId != 0) {
            int color = holder.itemView.getContext().getResources().getColor(colorResourceId, null);
            holder.txtNumLinea.setBackgroundColor(color);
        }

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