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
 * @author nitelow
 */
@Dao
public abstract class HospitalDao {//TODO aufsplitten nach Kategorie
    //Insert operations
    @Insert(onConflict = REPLACE)
    abstract void save(Hospital hospital);

    @Insert(onConflict = REPLACE)
    abstract void save(User user);

    @Insert(onConflict = REPLACE)
    abstract void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    abstract void save(Report report);

    @Insert(onConflict = REPLACE)
    abstract void save(Group group);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    abstract LiveData<Hospital> loadUserHospital(int userId);

    @Transaction
    @Query("SELECT * FROM users WHERE users.id = :userId")
    abstract LiveData<UserProfileInfo> loadUserProfile(int userId);

    @Transaction
    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    abstract LiveData<HospitalDump> loadHospitalDump(int userId);

    @Transaction
    @Query("SELECT * FROM reports WHERE device = :deviceId AND id = :reportId")
    abstract LiveData<ReportInfo> loadReportInfo(int deviceId, int reportId);

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    abstract LiveData<UserInfo> loadUserInfo(int userId);

    @Query("SELECT MAX(id) FROM reports WHERE device = :deviceId")
    abstract int getMaxReportId(int deviceId);

    @Query("SELECT * FROM users")
    abstract List<User> getUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    abstract LiveData<User> loadUser(int id);

    @Transaction
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    abstract LiveData<DeviceInfo> loadDevice(int deviceId);

    @Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastSync >= :currentMillis-(:timeout*1000)")
    abstract int hasUser(int id, long currentMillis, int timeout);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    abstract Hospital getUserHospital(int userId);

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    abstract LiveData<List<User>> loadUserColleagues(int userId);

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    abstract List<User> getUserColleagues(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    abstract LiveData<List<DeviceInfo>> loadHospitalDevices(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    abstract List<DeviceInfo> getHospitalDevices(int userId);

    @Query("DELETE FROM devices")
    abstract void deleteDevices();

    @Query("DELETE FROM reports")
    abstract void deleteReports();

    @Transaction
    void deleteGroupSpecificData() {
        deleteDevices();
        deleteReports();
    }
}
