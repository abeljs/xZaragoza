package abeljs.xzaragoza.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Buses.class, Postes.class, BusPostes.class, Favoritos.class, Noticias.class}, version = 1)
@TypeConverters({Conversores.class})
public abstract class BaseDeDatos extends RoomDatabase {

    public static String NOMBRE = "BaseDeDatosBuses";

    public abstract BusesDao daoBus();

    public abstract PostesDao daoPoste();

    public abstract BusPostesDao daoBusPostes();

    public abstract  FavoritosDao daoFavoritos();

    public abstract NoticiasDao daoNoticias();
}
