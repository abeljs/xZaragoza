package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "favoritos",
        primaryKeys = {"num_poste"},
        foreignKeys = @ForeignKey(entity = Postes.class, parentColumns = "num_poste", childColumns = "num_poste", onDelete = ForeignKey.NO_ACTION))
public class Favoritos {

    @NonNull
    @ColumnInfo(name = "num_poste")
    public String numPoste;

    @ColumnInfo(name = "nombre_favorito")
    public String nombreFavorito;

    public Favoritos(String numPoste, String nombreFavorito) {
        this.numPoste = numPoste;
        this.nombreFavorito = nombreFavorito;
    }
}
