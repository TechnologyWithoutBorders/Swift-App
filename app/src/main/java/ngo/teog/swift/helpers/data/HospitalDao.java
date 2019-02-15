package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HospitalDao {
    @Insert(onConflict = REPLACE)
    void save(Hospital hospital);

    @Query("SELECT * FROM hospital WHERE id = :id")
    LiveData<Hospital> load(int id);

    @Query("SELECT COUNT(*) FROM hospital WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasHospital(int id, long currentMillis, int timeout);
}
