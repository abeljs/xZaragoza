package abeljs.xzaragoza.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import abeljs.xzaragoza.ZgzBusIntents;
import abeljs.xzaragoza.apis.BusquedaTemperaturaAPI;
import abeljs.xzaragoza.apis.BusquedaTemperaturaCallback;
import abeljs.xzaragoza.data.Temperatura;

public class CargaTiempoService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BusquedaTemperaturaAPI temperaturaAPI = new BusquedaTemperaturaAPI();
        temperaturaAPI.getTemperatura(new BusquedaTemperaturaCallback() {

            @Override
            public void onBusquedaTemperaturaComplete(Temperatura temperatura) {
                Intent intent = new Intent(ZgzBusIntents.EL_TIEMPO_CARGADO_OK);
                intent.putExtra(ZgzBusIntents.EL_TIEMPO_RESULTADO, temperatura);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }

            @Override
            public void onBusquedaTemperaturaError(String cadenaError) {
                Log.e("pruebaService", "onStartCommand");
                Intent intent = new Intent(ZgzBusIntents.EL_TIEMPO_CARGADO_ERROR);
                intent.putExtra(ZgzBusIntents.MENSAJE_ERROR, cadenaError);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
        return flags;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
