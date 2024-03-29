package ngo.teog.swift.helpers.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Definition of database containing all information about a hospital.
 * @author nitelow
 */
@Database(entities = {User.class, Hospital.class, HospitalDevice.class, Report.class, OrganizationalUnit.class, ImageUploadJob.class, Observable.class}, version = 11)
@TypeConverters({Converters.class})
public abstract class HospitalDatabase extends RoomDatabase {
    public abstract HospitalDao getHospitalDao();
}
