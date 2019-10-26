package ngo.teog.swift.helpers.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HospitalDao {
    @Insert(onConflict = REPLACE)
    void save(Hospital hospital);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    LiveData<Hospital> loadUserHospital(int userId);

    @Transaction
    @Query("SELECT * FROM users WHERE users.id = :userId")
    LiveData<UserProfileInfo> loadUserProfile(int userId);

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Insert(onConflict = REPLACE)
    void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Transaction
    @Query("SELECT * FROM reports WHERE device = :deviceId AND id = :reportId")
    LiveData<ReportInfo> loadReportInfo(int deviceId, int reportId);

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
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    LiveData<DeviceInfo> loadDevice(int deviceId);

    @Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastSync >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);

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
