package abeljs.xzaragoza.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "bus_postes",
        primaryKeys = {"num_poste", "num_bus"},
        foreignKeys = {
                @ForeignKey(entity = Buses.class, parentColumns = "num_bus", childColumns = "num_bus", onDelete = ForeignKey.NO_ACTION),
                @ForeignKey(entity = Postes.class, parentColumns = "num_poste", childColumns = "num_poste", onDelete = ForeignKey.NO_ACTION)
        },
        indices = {@Index(value="num_bus"), @Index(value="num_poste"),})
public class BusPostes {

    // Del 24-35 (Estos incluidos) no tienen las paradas puestas en el xml

    @NonNull
    @ColumnInfo(name = "num_poste")
    public String numPoste;

    @NonNull
    @ColumnInfo(name = "num_bus")
    public String numBus;

    @ColumnInfo(name = "destino")
    public String destino;

    @ColumnInfo(name = "orden")
    public int orden;

    public BusPostes(String numPoste, String numBus, String destino, int orden) {
        this.numPoste = numPoste;
        this.numBus = numBus;
        this.destino = destino;
        this.orden = orden;
    }
}
