package abeljs.xzaragoza.apis;


import abeljs.xzaragoza.data.Temperatura;

public interface BusquedaTemperaturaCallback {
    void onBusquedaTemperaturaComplete(Temperatura result);
    void onBusquedaTemperaturaError(String cadenaError);
}
