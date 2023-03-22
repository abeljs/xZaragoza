package abeljs.xzaragoza.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LineaDeBus.class}, version = 1)
public abstract class BaseDeDatos extends RoomDatabase {

    public abstract DaoLineaDeBus daoLineaDeBus();
}
