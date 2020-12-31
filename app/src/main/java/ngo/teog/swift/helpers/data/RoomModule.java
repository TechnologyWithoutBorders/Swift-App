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
    private final HospitalDatabase hospitalDatabase;

    public RoomModule(Application mApplication) {
        hospitalDatabase = Room.databaseBuilder(mApplication, HospitalDatabase.class, "hospital-db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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
    HospitalRepository hospitalRepository(HospitalDao hospitalDao, Context context) {
        return new HospitalRepository(hospitalDao, context);
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //ignore as no noteworthy changes took place
        }
    };

    /*
     * Hospital column was added to table Reports
     */
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reports ADD COLUMN hospital INTEGER NOT NULL DEFAULT 1");
        }
    };

    /**
     * Title column was added to table Reports
     */
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reports ADD COLUMN title TEXT");
        }
    };

    /*
     * Ward column was renamed to location
     */
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Rename statement ist not supported by older Android versions, so we have to go the old way
            database.beginTransaction();
            try {
                database.execSQL("CREATE TABLE devices_tmp(id INTEGER NOT NULL, assetNumber TEXT, type TEXT, serialNumber TEXT, manufacturer TEXT, model TEXT, location TEXT, hospital INTEGER NOT NULL, maintenanceInterval INTEGER NOT NULL, lastUpdate INTEGER, lastSync INTEGER, PRIMARY KEY(id, hospital))");
                database.execSQL("INSERT INTO devices_tmp(id, assetNumber, type, serialNumber, manufacturer, model, location, hospital, maintenanceInterval, lastUpdate, lastSync) SELECT id, assetNumber, type, serialNumber, manufacturer, model, ward, hospital, maintenanceInterval, lastUpdate, lastSync FROM devices");
                database.execSQL("DROP TABLE devices");
                database.execSQL("ALTER TABLE devices_tmp RENAME TO devices");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };
}
