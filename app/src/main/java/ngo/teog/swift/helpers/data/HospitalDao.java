package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HospitalDao {
    @Insert(onConflict = REPLACE)
    void save(Hospital hospital);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    LiveData<Hospital> loadUserHospital(int userId);

    @Query("SELECT COUNT(*) FROM hospitals WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasHospital(int id, long currentMillis, int timeout);

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Insert(onConflict = REPLACE)
    void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> loadUser(int id);

    @Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);

    @Query("SELECT hospital from users WHERE id = :id")
    int getHospital(int id);

    @Query("SELECT * from users WHERE hospital = :id")
    LiveData<List<User>> getHospitalMembers(int id);
}
