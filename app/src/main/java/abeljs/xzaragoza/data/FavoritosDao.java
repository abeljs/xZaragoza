package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoritosDao {

    @Query("SELECT * FROM favoritos")
    List<Favoritos> getFavoritos();

    @Query("SELECT * FROM favoritos " +
            "WHERE num_poste LIKE :num_poste")
    List<Favoritos> getFavoritoPorPoste(String num_poste);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarFavorito(Favoritos favorito);

    @Delete
    void borrarFavorito(Favoritos favorito);
}
