package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserDao;

@Database(entities = {User.class, Hospital.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
    public abstract HospitalDao getHospitalDao();
}
