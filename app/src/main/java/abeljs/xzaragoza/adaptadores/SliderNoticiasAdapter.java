package abeljs.xzaragoza.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.R;
import abeljs.xzaragoza.data.Noticias;

public class SliderNoticiasAdapter extends SliderViewAdapter<SliderNoticiasAdapter.SliderAdapterViewHolder> {

    private final Context contexto;
    private List<Noticias> listaNoticias;

    public SliderNoticiasAdapter(Context context, List<Noticias> listaNoticias) {
        this.contexto = context;
        this.listaNoticias = listaNoticias;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderAdapterViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {
        Noticias noticia = listaNoticias.get(position);
        viewHolder.txtNoticia.setText(noticia.titulo);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(noticia.url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                contexto.startActivity(intent);
            }
        });

    }

    public void renewItems(List<Noticias> listaNoticias) {
        this.listaNoticias = listaNoticias;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listaNoticias.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        TextView txtNoticia;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            txtNoticia = itemView.findViewById(R.id.txtTitularNoticia);
            this.itemView = itemView;
        }
    }
}
