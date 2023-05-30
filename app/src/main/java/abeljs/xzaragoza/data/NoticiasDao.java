package abeljs.xzaragoza.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface NoticiasDao {

    @Query("SELECT * FROM noticias ORDER BY fecha_vista LIMIT 5")
    List<Noticias> getCincoNoticiasRecientes();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertarNoticia(Noticias notcia);

    @Query("DELETE FROM noticias WHERE fecha < :fechaViejo")
    void limpiarNoticias(Date fechaViejo);

    @Update
    void cambiarFechaVista(Noticias noticia);
}
