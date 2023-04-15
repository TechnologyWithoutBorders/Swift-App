package ngo.teog.swift;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import ngo.teog.swift.helpers.data.HospitalDatabase;
import ngo.teog.swift.helpers.data.RoomModule;

@RunWith(AndroidJUnit4.class)
public class DatabaseMigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public final MigrationTestHelper helper;

    public DatabaseMigrationTest() {
        helper = new MigrationTestHelper(
                InstrumentationRegistry.getInstrumentation(),
                HospitalDatabase.class);
    }

    @Test
    public void migrate2To3() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        //db.execSQL(...);

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_2_3 as the migration process.
        helper.runMigrationsAndValidate(TEST_DB, 3, true, RoomModule.MIGRATION_2_3);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }

    @Test
    public void migrate3To4() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 4, true, RoomModule.MIGRATION_3_4);
    }

    @Test
    public void migrate4To5() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 4);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 5, true, RoomModule.MIGRATION_4_5);
    }

    @Test
    public void migrate5To6() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 5);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 6, true, RoomModule.MIGRATION_5_6);
    }

    @Test
    public void migrate6To7() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 7, true, RoomModule.MIGRATION_6_7);
    }

    @Test
    public void migrate7To8() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 7);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 8, true, RoomModule.MIGRATION_7_8);
    }

    @Test
    public void migrate8To9() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 8);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 9, true, RoomModule.MIGRATION_8_9);
    }

    @Test
    public void migrate9To10() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 9);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 10, true, RoomModule.MIGRATION_9_10);
    }

    @Test
    public void migrate10To11() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 10);
        db.close();

        helper.runMigrationsAndValidate(TEST_DB, 11, true, RoomModule.MIGRATION_10_11);
    }
}