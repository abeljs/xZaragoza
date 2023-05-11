package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BusesDao {

    @Query("SELECT * FROM buses ORDER BY num_bus")
    List<Buses> getAllBuses();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarBus(Buses bus);
}
