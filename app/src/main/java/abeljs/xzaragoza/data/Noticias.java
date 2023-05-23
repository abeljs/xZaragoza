package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "noticias")
public class Noticias {

    @PrimaryKey
    @ColumnInfo(name = "url")
    @NonNull
    public String url;

    @ColumnInfo(name = "titulo")
    public String titulo;

    @ColumnInfo(name = "fecha")
    public Date fecha;

    @ColumnInfo(name = "fecha_vista")
    public Date fechaVista = null;

    public Noticias(String url, String titulo, Date fecha) {
        this.url = url;
        this.titulo = titulo;
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Noticias{" +
                "url='" + url + '\'' +
                ", titulo='" + titulo + '\'' +
                ", fecha=" + fecha +
                ", fechaVista=" + fechaVista +
                '}';
    }
}
