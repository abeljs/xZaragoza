package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lineaBus")
public class LineaDeBus {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "numLinea")
    @NonNull
    public String numLinea;

    @ColumnInfo(name = "direccion1")
    public String direccion1;

    @ColumnInfo(name = "direccion2")
    public String direccion2;


    public LineaDeBus(String numLinea, String direccion1, String direccion2) {
        this.numLinea = numLinea;
        this.direccion1 = direccion1;
        this.direccion2 = direccion2;
    }
}
