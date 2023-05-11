package abeljs.xzaragoza.apis;

import java.util.ArrayList;

import abeljs.xzaragoza.data.TiempoBus;

public interface BusquedaTiemposPosteCallback {
    void onBusquedaTiemposPosteComplete(ArrayList<TiempoBus> result);
    void onBusquedaTiemposPosteError(String cadenaError);
}
