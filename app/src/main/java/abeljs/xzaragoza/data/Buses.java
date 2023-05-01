package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "buses")
public class Buses {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "num_bus")
    @NonNull
    public String numBus;

    @ColumnInfo(name = "direccion1")
    public String direccion1;

    @ColumnInfo(name = "direccion2")
    public String direccion2;


    public Buses(String numBus, String direccion1, String direccion2) {
        this.numBus = numBus;
        this.direccion1 = direccion1;
        this.direccion2 = direccion2;
    }
}
