package abeljs.xzaragoza.apis;

import java.util.List;

import abeljs.xzaragoza.data.Buses;

public interface BusquedaLineasDeBusesCallback {
    void onBusquedaLineasDeBusesComplete(List<Buses> result);
    void onBusquedaLineasDeBusesError(String cadenaError);
}
