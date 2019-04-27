package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HospitalDao {
    @Insert(onConflict = REPLACE)
    void save(Hospital hospital);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    LiveData<Hospital> loadUserHospital(int userId);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = :hospitalId")
    LiveData<Hospital> loadHospital(int hospitalId);

    @Query("SELECT COUNT(*) FROM hospitals WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasHospital(int id, long currentMillis, int timeout);

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Insert(onConflict = REPLACE)
    void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Insert(onConflict = REPLACE)
    void save(List<Report> reports);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> loadUser(int id);

    @Transaction
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    LiveData<DeviceInfo> loadDevice(int deviceId);

    @Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);

    @Query("SELECT hospital from users WHERE id = :id")
    int getHospital(int id);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    Hospital getUserHospital(int userId);

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    LiveData<List<User>> loadUserColleagues(int userId);

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    List<User> getUserColleagues(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    LiveData<List<DeviceInfo>> loadHospitalDevices(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    List<DeviceInfo> getHospitalDevices(int userId);
}
