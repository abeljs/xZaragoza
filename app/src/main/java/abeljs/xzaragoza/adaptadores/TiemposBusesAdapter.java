package abeljs.xzaragoza.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.TiempoBus;


public class TiemposBusesAdapter extends RecyclerView.Adapter<TiemposBusesAdapter.ViewHolder> {

    private List<TiempoBus> listaBusesEnParada;

    public TiemposBusesAdapter(List<TiempoBus> listaBuses) {
        this.listaBusesEnParada = listaBuses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_linea_bus,parent,false);
        return new ViewHolder(vista);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TiempoBus bus = listaBusesEnParada.get(position);

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