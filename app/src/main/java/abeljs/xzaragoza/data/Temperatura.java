package abeljs.xzaragoza.data;

public class Temperatura {

    public int temperaturaActual;
    public int temperaturaMinima;
    public int temperaturaMaxima;
    public String estadoDeCielo;


    public Temperatura(int temperaturaActual, int temperaturaMinima, int temperaturaMaxima, String estadoDeCielo) {
        this.temperaturaActual = temperaturaActual;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
        this.estadoDeCielo = estadoDeCielo;
    }
}
