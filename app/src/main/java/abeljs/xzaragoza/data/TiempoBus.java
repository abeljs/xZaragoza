package abeljs.xzaragoza.data;

import java.io.Serializable;


public class TiempoBus implements Serializable {

//    @ColumnInfo(name = "numParada")
    public String numParada;

//    @ColumnInfo(name = "numLinea")
    public String numBus;

//    @ColumnInfo(name = "primero")
    public int minutos;

//    @ColumnInfo(name = "segundo")
//    public String segundo;

    public TiempoBus(String numParada, String numLinea, int minutos) {
        this.numParada = numParada;
        this.numBus = numLinea;
        this.minutos = minutos;
    }
}
