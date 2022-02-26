package ngo.teog.swift.helpers.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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
public abstract class HospitalDao {
    @Insert(onConflict = REPLACE)
    public abstract void save(Hospital hospital);

    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    public abstract LiveData<Hospital> loadUserHospital(int userId);

    @Transaction
    @Query("SELECT * FROM hospitals WHERE hospitals.id = (SELECT hospital FROM users WHERE users.id = :userId)")
    public abstract LiveData<HospitalDump> loadHospitalDump(int userId);

    @Insert(onConflict = REPLACE)
    public abstract void save(User user);

    @Insert(onConflict = REPLACE)
    public abstract void save(HospitalDevice device);

    @Insert(onConflict = REPLACE)
    public abstract void save(Report report);

    @Insert(onConflict = REPLACE)
    public abstract void save(Observable observable);

    @Insert(onConflict = REPLACE)
    public abstract void save(ImageUploadJob imageUploadJob);

    @Query("DELETE FROM image_upload_jobs WHERE deviceId = :deviceId")
    public abstract void deleteImageUploadJob(int deviceId);

    @Transaction
    @Query("SELECT * FROM reports WHERE (SELECT hospital from devices WHERE reports.device = :deviceId) = (SELECT hospital FROM users WHERE users.id = :userId) AND reports.id = :reportId")
    public abstract LiveData<ReportInfo> loadReportInfo(int userId, int deviceId, int reportId);

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    public abstract LiveData<UserInfo> loadUserInfo(int userId);

    @Query("SELECT MAX(id) FROM reports WHERE device = :deviceId")
    public abstract int getMaxReportId(int deviceId);

    @Query("SELECT * FROM users")
    public abstract List<User> getUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    public abstract LiveData<User> loadUser(int id);

    @Query("SELECT * FROM observables WHERE id = :id")
    public abstract LiveData<Observable> loadObservable(int id);

    @Query("SELECT * FROM image_upload_jobs")
    public abstract List<ImageUploadJob> getImageUploadJobs();

    @Transaction
    @Query("SELECT * FROM devices WHERE devices.hospital = (SELECT hospital FROM users WHERE users.id = :userId) AND devices.id = :deviceId")
    public abstract LiveData<DeviceInfo> loadDevice(int userId, int deviceId);

    @Transaction
    @Query("SELECT * FROM devices WHERE devices.hospital = (SELECT hospital FROM users WHERE users.id = :userId) AND devices.id = :deviceId")
    public abstract DeviceInfo getDevice(int userId, int deviceId);

    /*@Query("SELECT COUNT(*) FROM users WHERE id = :id AND lastSync >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);*/ //TODO

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    public abstract LiveData<List<User>> loadUserColleagues(int userId);

    @Query("SELECT * from users WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    public abstract List<User> getUserColleagues(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    public abstract LiveData<List<DeviceInfo>> loadHospitalDevices(int userId);

    @Transaction
    @Query("SELECT * from devices WHERE hospital = (SELECT hospital from users WHERE id = :userId)")
    public abstract List<DeviceInfo> getHospitalDevices(int userId);

    @Transaction
    public void addReport(Report report) {
        int maxReportId = getMaxReportId(report.getDevice());
        report.setId(maxReportId+1);
        save(report);
    }
}
