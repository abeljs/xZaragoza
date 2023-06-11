package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostesDao {

    @Query("SELECT * FROM postes")
    List<Postes> getPostes();

    @Query("SELECT * FROM postes " +
            "WHERE num_poste LIKE :num_poste")
    List<Postes> getPoste(String num_poste);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarPoste(Postes poste);

}
