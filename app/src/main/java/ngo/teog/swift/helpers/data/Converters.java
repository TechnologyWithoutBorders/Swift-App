package ngo.teog.swift.helpers.data;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * This class is used for converting complex data structures into simple ones
 * in order to store them in the database. These will be used automatically.
 * @author nitelow
 */
public class Converters {

    /**
     * Converts a given timestamp into a Date object.
     * @param value Timestamp
     * @return Date
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Converts a given Date object into a timestamp.
     * @param date Date
     * @return Timestamp
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
