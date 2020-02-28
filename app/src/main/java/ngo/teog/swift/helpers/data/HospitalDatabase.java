package ngo.teog.swift.helpers.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Hospital.class, HospitalDevice.class, Report.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class HospitalDatabase extends RoomDatabase {
    public abstract HospitalDao getHospitalDao();
}
