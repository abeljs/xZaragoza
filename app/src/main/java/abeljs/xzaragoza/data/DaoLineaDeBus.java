package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import abeljs.xzaragoza.data.LineaDeBus;

@Dao
public interface DaoLineaDeBus {

    @Query("SELECT * FROM lineaBus ORDER BY numLinea")
    List<LineaDeBus> getAllLineasDeBus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarLineasDeBus(LineaDeBus lineaDeBus);
}
