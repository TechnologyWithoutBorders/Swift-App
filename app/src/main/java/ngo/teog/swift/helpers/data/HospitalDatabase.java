package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {User.class, Hospital.class, HospitalDevice.class, Report.class}, version = 1, exportSchema = false)
public abstract class HospitalDatabase extends RoomDatabase {
    public abstract HospitalDao getHospitalDao();
}
