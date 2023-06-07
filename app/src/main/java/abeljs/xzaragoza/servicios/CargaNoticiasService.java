package abeljs.xzaragoza.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import java.util.Calendar;
import java.util.List;

import abeljs.xzaragoza.ZgzBusIntents;
import abeljs.xzaragoza.adaptadores.SliderNoticiasAdapter;
import abeljs.xzaragoza.apis.BusquedaBusesAPI;
import abeljs.xzaragoza.apis.BusquedaBusesCallback;
import abeljs.xzaragoza.apis.BusquedaNoticiasAPI;
import abeljs.xzaragoza.apis.BusquedaNoticiasCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;
import abeljs.xzaragoza.data.Noticias;
import abeljs.xzaragoza.data.NoticiasDao;

public class CargaNoticiasService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        BusquedaNoticiasAPI noticiasAPI = new BusquedaNoticiasAPI();
        noticiasAPI.getNoticias(new BusquedaNoticiasCallback() {
            @Override
            public void onBusquedaNoticiasComplete(List<Noticias> listaNoticias) {
                BaseDeDatos db = Room.databaseBuilder(getApplicationContext(),
                        BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
                NoticiasDao daoNoticias = db.daoNoticias();

                for (Noticias noticia : listaNoticias) {
                    daoNoticias.insertarNoticia(noticia);
                }
                Intent intent = new Intent(ZgzBusIntents.NOTICIAS_CARGADAS_OK);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onBusquedaNoticiasError(String cadenaError) {
                Intent intent = new Intent(ZgzBusIntents.NOTICIAS_CARGADAS_ERROR);
                intent.putExtra(ZgzBusIntents.MENSAJE_ERROR, cadenaError);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
