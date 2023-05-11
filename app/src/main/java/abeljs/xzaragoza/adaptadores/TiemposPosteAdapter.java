package abeljs.xzaragoza.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.TiempoBus;


public class TiemposPosteAdapter extends RecyclerView.Adapter<TiemposPosteAdapter.ViewHolder> {

    private List<TiempoBus> listaBusesEnParada;

    public TiemposPosteAdapter(List<TiempoBus> listaBuses) {
        this.listaBusesEnParada = listaBuses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_bus,parent,false);
        return new ViewHolder(vista);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TiempoBus bus = listaBusesEnParada.get(position);

        int colorResourceId = holder.itemView.getContext().getResources().getIdentifier("color_" + bus.numBus, "color", holder.itemView.getContext().getPackageName());
        if (colorResourceId != 0) {
            int color = holder.itemView.getContext().getResources().getColor(colorResourceId, null);
            holder.txtNumLinea.setBackgroundColor(color);
        }

        holder.txtNumLinea.setText(bus.numBus);

        if (bus.minutos == 0) {
            holder.txtRecorrido.setText("En la parada");
        } else {
            holder.txtRecorrido.setText(String.valueOf(bus.minutos) + " minutos");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaBusesEnParada.size();
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