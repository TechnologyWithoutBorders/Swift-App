package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void save(User user);
    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<User> load(int id);
    @Query("SELECT COUNT(*) FROM user WHERE id = :id AND lastUpdate >= :timeout")
    int hasUser(int id, long timeout);
}
