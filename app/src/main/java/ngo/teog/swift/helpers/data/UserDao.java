package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void save(User user);

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<User> load(int id);

    @Query("SELECT COUNT(*) FROM user WHERE id = :id AND lastUpdate >= :currentMillis-(:timeout*1000)")
    int hasUser(int id, long currentMillis, int timeout);

    @Query("SELECT hospital from user WHERE id = :id")
    int getHospital(int id);
}
