package abeljs.xzaragoza.apis;

import java.util.ArrayList;

import abeljs.xzaragoza.data.Postes;

public interface BusquedaPostesCallback {
    void onBusquedaPostesComplete(ArrayList<Postes> result);
    void onBusquedaPostesError(String cadenaError);
}
