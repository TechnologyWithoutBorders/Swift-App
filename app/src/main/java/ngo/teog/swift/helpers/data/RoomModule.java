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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
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
     * Title column was added to table Reports, ward column was renamed to location
     */
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reports ADD COLUMN title TEXT");

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

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS observables (id INTEGER NOT NULL, PRIMARY KEY(id))");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Add the hospital column to primary key. This requires all report data to be copied into a new table.
            database.beginTransaction();
            try {
                database.execSQL("CREATE TABLE reports_tmp(id INTEGER NOT NULL, author INTEGER NOT NULL, title TEXT, device INTEGER NOT NULL, hospital INTEGER NOT NULL, previousState INTEGER NOT NULL, currentState INTEGER NOT NULL, description TEXT, created INTEGER, lastSync INTEGER, PRIMARY KEY(id, device, hospital))");
                database.execSQL("INSERT INTO reports_tmp(id, author, title, device, hospital, previousState, currentState, description, created, lastSync) SELECT id, author, title, device, hospital, previousState, currentState, description, created, lastSync FROM reports");
                database.execSQL("DROP TABLE reports");
                database.execSQL("ALTER TABLE reports_tmp RENAME TO reports");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Drop column is not supported by SQLite, so we need to create a new table
            database.beginTransaction();
            try {
                database.execSQL("CREATE TABLE reports_tmp(id INTEGER NOT NULL, author INTEGER NOT NULL, title TEXT, device INTEGER NOT NULL, hospital INTEGER NOT NULL, currentState INTEGER NOT NULL, description TEXT, created INTEGER, lastSync INTEGER, PRIMARY KEY(id, device, hospital))");
                database.execSQL("INSERT INTO reports_tmp(id, author, title, device, hospital, currentState, description, created, lastSync) SELECT id, author, title, device, hospital, currentState, description, created, lastSync FROM reports");
                database.execSQL("DROP TABLE reports");
                database.execSQL("ALTER TABLE reports_tmp RENAME TO reports");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS image_upload_jobs (deviceId INTEGER NOT NULL, created INTEGER, PRIMARY KEY(deviceId))");
        }
    };

    /**
     * Adds table organizational_units and adds organizationalUnit to devices table
     */
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS organizational_units (id INTEGER NOT NULL, hospital INTEGER NOT NULL, name TEXT, lastUpdate INTEGER, PRIMARY KEY(id, hospital))");

            database.beginTransaction();
            try {
                database.execSQL("CREATE TABLE devices_tmp(id INTEGER NOT NULL, assetNumber TEXT, type TEXT, serialNumber TEXT, manufacturer TEXT, model TEXT, organizationalUnit INTEGER, hospital INTEGER NOT NULL, maintenanceInterval INTEGER NOT NULL, lastUpdate INTEGER, lastSync INTEGER, PRIMARY KEY(id, hospital))");
                database.execSQL("INSERT INTO devices_tmp(id, assetNumber, type, serialNumber, manufacturer, model, organizationalUnit, hospital, maintenanceInterval, lastUpdate, lastSync) SELECT id, assetNumber, type, serialNumber, manufacturer, model, null, hospital, maintenanceInterval, lastUpdate, lastSync FROM devices");
                database.execSQL("DROP TABLE devices");
                database.execSQL("ALTER TABLE devices_tmp RENAME TO devices");
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    };
}
