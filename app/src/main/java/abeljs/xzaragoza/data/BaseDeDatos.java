package abeljs.xzaragoza.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Buses.class, Postes.class, BusPostes.class}, version = 1)
public abstract class BaseDeDatos extends RoomDatabase {

    public static String NOMBRE = "BaseDeDatosBuses";

    public abstract BusesDao daoBus();

    public abstract PostesDao daoPoste();

    public abstract BusPostesDao daoBusPostes();
}
