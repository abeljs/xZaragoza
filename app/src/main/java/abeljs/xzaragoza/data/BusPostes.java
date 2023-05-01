package abeljs.xzaragoza.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "paradaBus-lineaBus",
        primaryKeys = {"numParada", "numLinea"},
        foreignKeys = {
                @ForeignKey(entity = Buses.class, parentColumns = "numPoste", childColumns = "numPoste", onDelete = 5),
                @ForeignKey(entity = Postes.class, parentColumns = "numBus", childColumns = "numBus", onDelete = 5)
        })
public class BusPostes {

    @ColumnInfo(name = "numPoste")
    public String numPoste;

    @ColumnInfo(name = "numBus")
    public String numBus;

    @ColumnInfo(name = "orden")
    public int orden;

    public BusPostes(String numPoste, String numBus, int orden) {
        this.numPoste = numPoste;
        this.numBus = numBus;
        this.orden = orden;
    }
}
