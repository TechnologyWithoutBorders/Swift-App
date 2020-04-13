package ngo.teog.swift.helpers.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private HospitalDatabase hospitalDatabase;

    public RoomModule(Application mApplication) {
        hospitalDatabase = Room.databaseBuilder(mApplication, HospitalDatabase.class, "hospital-db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
    }

    @Singleton
    @Provides
    HospitalDatabase providesRoomDatabase() {
        return hospitalDatabase;
    }

    @Singleton
    @Provides
    HospitalDao providesHospitalDao(HospitalDatabase hospitalDatabase) {
        return hospitalDatabase.getHospitalDao();
    }

    @Singleton
    @Provides
    HospitalRepository userRepository(HospitalDao hospitalDao, Context context) {
        return new HospitalRepository(hospitalDao, context);
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //ignore as no noteworthy changes took place
        }
    };

    /*
     * Hospital column was added to table Reports
     */
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reports ADD COLUMN hospital INTEGER");
            database.execSQL("UPDATE reports SET hospital = 1");
        }
    };
}
