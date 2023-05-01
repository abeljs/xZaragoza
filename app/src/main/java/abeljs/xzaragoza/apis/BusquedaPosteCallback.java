package abeljs.xzaragoza.apis;

import java.util.ArrayList;

import abeljs.xzaragoza.data.TiempoBus;

public interface BusquedaPosteCallback {
    void onBusquedaPosteComplete(ArrayList<TiempoBus> result);
    void onBusquedaPosteError(String cadenaError);
}
