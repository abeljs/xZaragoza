package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "postes")
public class Postes {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "num_poste")
    @NonNull
    public String numPoste;

    @ColumnInfo(name = "nombre_poste")
    public String nombrePoste;

//    public boolean esFavorito = false; crear tabla ParadaDeBusFavoritos dos campos, numParada y fechaAlta

    public Postes(@NonNull String numParada, String nombreParada) {
        this.numPoste = numParada;
        this.nombrePoste = nombreParada;
    }
}
