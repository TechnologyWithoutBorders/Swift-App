package ngo.teog.swift.helpers.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import ngo.teog.swift.helpers.export.HospitalDump;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * Definition of methods providing access to hospital objects in database.
 * @author nitelow
 */
@Dao
public interface HospitalDao {
    @Insert(onConflict = REPLACE)
    void save(Hospital hospital);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    LiveData<Hospital> loadUserHospital(int userId);

    @Transaction
    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    LiveData<HospitalDump> loadHospitalDump(int userId);

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Insert(onConflict = REPLACE)
    void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Transaction
    @Query("SELECT * FROM reports WHERE (SELECT hospital from devices WHERE reports.device = :deviceId) = (SELECT hospital FROM users WHERE users.id = :userId) AND reports.id = :reportId")
    LiveData<ReportInfo> loadReportInfo(int userId, int deviceId, int reportId);

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserInfo> loadUserInfo(int userId);

    @Query("SELECT MAX(id) FROM reports WHERE device = :deviceId")
    int getMaxReportId(int deviceId);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> loadUser(int id);

    @Transaction
    @Query("SELECT * FROM devices WHERE devices.hospital = (SELECT hospital FROM users WHERE users.id = :userId) AND devices.id = :deviceId")
    LiveData<DeviceInfo> loadDevice(int userId, int deviceId);

    /*@Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastSync >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);*/ //TODO

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
