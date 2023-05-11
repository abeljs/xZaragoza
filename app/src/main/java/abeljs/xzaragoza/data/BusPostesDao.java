package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BusPostesDao {

    @Query("SELECT * FROM bus_postes " +
            "WHERE UPPER(destino) LIKE UPPER('%' || :destinoBusqueda || '%') AND " +
            "(num_bus) LIKE UPPER(:numBus) " +
            "ORDER BY orden")
    List<BusPostes> getBusPostesPorDestinoBus(String destinoBusqueda, String numBus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarBusPostes(BusPostes busPoste);
}
