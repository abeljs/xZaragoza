package abeljs.xzaragoza.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Buses.class}, version = 1)
public abstract class BaseDeDatos extends RoomDatabase {

    public static String NOMBRE = "BaseDeDatosBuses";

    public abstract DaoBuses daoLineaDeBus();
}
