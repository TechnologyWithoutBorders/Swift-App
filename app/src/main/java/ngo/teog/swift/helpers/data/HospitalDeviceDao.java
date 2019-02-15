package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HospitalDeviceDao {
    @Insert(onConflict = REPLACE)
    void save(HospitalDevice device);

    @Query("SELECT * FROM hospitalDevice WHERE id = :id")
    LiveData<HospitalDevice> load(int id);

    @Query("SELECT COUNT(*) FROM hospitalDevice WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasDevice(int id, long currentMillis, int timeout);
}
