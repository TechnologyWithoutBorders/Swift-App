package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {User.class, Hospital.class, HospitalDevice.class, Report.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class HospitalDatabase extends RoomDatabase {
    public abstract HospitalDao getHospitalDao();
}
