package abeljs.xzaragoza.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.BusPostes;
import abeljs.xzaragoza.data.Postes;
import abeljs.xzaragoza.data.PostesDao;
import abeljs.xzaragoza.fragments.BusPostesSelectedInterface;
import abeljs.xzaragoza.fragments.FragmentTiemposPoste;


public class BusPostesAdapter extends RecyclerView.Adapter<BusPostesAdapter.ViewHolder> {

    private final Context context;
    private final BusPostesSelectedInterface busPostesSelectedInterface;
    private final List<BusPostes> listaBusPostes;

    public BusPostesAdapter(Context context, BusPostesSelectedInterface busPostesSelectedInterface, List<BusPostes> listaBusPostes) {
        this.context = context;
        this.busPostesSelectedInterface = busPostesSelectedInterface;
        this.listaBusPostes = listaBusPostes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_poste,parent,false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BusPostes busPoste = listaBusPostes.get(position);
        holder.txtNumPoste.setText(busPoste.numPoste);

//        int colorResourceId = holder.itemView.getContext().getResources().getIdentifier("color_" + numBus, "color", holder.itemView.getContext().getPackageName());
//        if (colorResourceId != 0) {
//            int color = holder.itemView.getContext().getResources().getColor(colorResourceId, null);
//            holder.txtNumPoste.setBackgroundColor(color);
//        }

        ;
        holder.txtNombrePoste.setText(buscarNombrePoste(busPoste.numPoste).get(0).nombrePoste.trim());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busPostesSelectedInterface.onBusPostesSelected(busPoste);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaBusPostes.size();
    }


    public List<Postes> buscarNombrePoste(String numPoste) {
        BaseDeDatos db = Room.databaseBuilder(context,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        PostesDao daoPoste = db.daoPoste();

        return daoPoste.getPoste(numPoste);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumPoste, txtNombrePoste;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumPoste = itemView.findViewById(R.id.txtNumPoste);
            txtNombrePoste = itemView.findViewById(R.id.txtNombrePoste);
        }

    }
}