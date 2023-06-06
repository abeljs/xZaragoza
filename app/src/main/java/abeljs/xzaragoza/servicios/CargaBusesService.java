package abeljs.xzaragoza.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import java.util.List;

import abeljs.xzaragoza.ZgzBusIntents;
import abeljs.xzaragoza.apis.BusquedaBusesAPI;
import abeljs.xzaragoza.apis.BusquedaBusesCallback;
import abeljs.xzaragoza.data.BaseDeDatos;
import abeljs.xzaragoza.data.Buses;
import abeljs.xzaragoza.data.BusesDao;


public class CargaBusesService extends Service  {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("pruebaServicio", "esto es una prueba");

        BusquedaBusesAPI apiBusquedaBuses = new BusquedaBusesAPI();
        apiBusquedaBuses.getBuses(new BusquedaBusesCallback() {
            @Override
            public void onBusquedaLineasDeBusesComplete(List<Buses> listaBuses) {
                BaseDeDatos db = Room.databaseBuilder(getApplicationContext(),
                        BaseDeDatos.class, BaseDeDatos.NOMBRE).allowMainThreadQueries().build();
                BusesDao daoLineaDeBus = db.daoBus();

                for (Buses bus : listaBuses) {
                    daoLineaDeBus.insertarBus(bus);
                }

                Intent intent = new Intent(ZgzBusIntents.BUSES_CARGADOS_OK);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onBusquedaLineasDeBusesError(String cadenaError) {
                Intent intent = new Intent(ZgzBusIntents.BUSES_CARGADOS_ERROR);
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
