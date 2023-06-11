package abeljs.xzaragoza.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import abeljs.xzaragoza.ZgzBusIntents;
import abeljs.xzaragoza.apis.BusquedaPostesAPI;
import abeljs.xzaragoza.apis.BusquedaPostesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Postes;
import abeljs.xzaragoza.data.PostesDao;

public class CargaPostesService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        BusquedaPostesAPI apiBusquedaPostes = new BusquedaPostesAPI();
        apiBusquedaPostes.getPostes(new BusquedaPostesCallback() {
            @Override
            public void onBusquedaPostesComplete(ArrayList<Postes> result) {
                insertarPostesEnBD(result);
                Intent intent = new Intent(ZgzBusIntents.POSTES_CARGADOS_OK);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onBusquedaPostesError(String cadenaError) {
                Intent intent = new Intent(ZgzBusIntents.POSTES_CARGADOS_ERROR);
                intent.putExtra(ZgzBusIntents.MENSAJE_ERROR, cadenaError);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

        });

        return super.onStartCommand(intent, flags, startId);
    }

    public void insertarPostesEnBD(List<Postes> listaPostes) {
        BaseDeDatos db = Room.databaseBuilder(this,
                BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
        PostesDao daoPoste = db.daoPostes();

        for (Postes poste : listaPostes) {
            daoPoste.insertarPoste(poste);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
