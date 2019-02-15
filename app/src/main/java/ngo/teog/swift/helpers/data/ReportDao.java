package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ReportDao {
    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Query("SELECT * FROM report WHERE id = :id")
    LiveData<Report> load(int id);

    @Query("SELECT COUNT(*) FROM report WHERE id = :id")
    int hasReport(int id);
}
