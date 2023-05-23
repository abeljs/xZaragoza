package abeljs.xzaragoza.apis;

import java.util.List;

import abeljs.xzaragoza.data.Noticias;

public interface BusquedaNoticiasCallback {
    void onBusquedaNoticiasComplete(List<Noticias> result);
    void onBusquedaNoticiasError(String cadenaError);
}
