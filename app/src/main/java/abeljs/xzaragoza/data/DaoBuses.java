package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoBuses {

    @Query("SELECT * FROM buses ORDER BY num_bus")
    List<Buses> getAllLineasDeBus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarLineasDeBus(Buses lineaDeBus);
}
