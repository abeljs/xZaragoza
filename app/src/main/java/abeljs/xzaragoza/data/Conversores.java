package abeljs.xzaragoza.data;

import androidx.room.TypeConverter;

import java.util.Date;

public class Conversores {

    @TypeConverter
    public static Date longAFecha(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fechaALong(Date date) {
        return date == null ? null : date.getTime();
    }

}
