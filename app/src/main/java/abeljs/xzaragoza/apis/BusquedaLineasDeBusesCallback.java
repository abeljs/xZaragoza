package abeljs.xzaragoza.apis;

import java.util.List;

import abeljs.xzaragoza.data.LineaDeBus;

public interface BusquedaLineasDeBusesCallback {
    void onBusquedaLineasDeBusesComplete(List<LineaDeBus> result);
    void onBusquedaLineasDeBusesError(String cadenaError);
}
