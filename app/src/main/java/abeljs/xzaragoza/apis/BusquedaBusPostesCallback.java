package abeljs.xzaragoza.apis;

import java.util.List;

import abeljs.xzaragoza.data.BusPostes;

public interface BusquedaBusPostesCallback {
    void onBusquedaBusPostesComplete(List<BusPostes> result);
    void onBusquedaBusPostesError(String cadenaError);
}
