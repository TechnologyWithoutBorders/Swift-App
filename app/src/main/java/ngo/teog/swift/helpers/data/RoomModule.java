package ngo.teog.swift.helpers.data;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private UserDatabase userDatabase;

    public RoomModule(Application mApplication) {
        userDatabase = Room.databaseBuilder(mApplication, UserDatabase.class, "user-db").build();
    }

    @Singleton
    @Provides
    UserDatabase providesRoomDatabase() {
        return userDatabase;
    }

    @Singleton
    @Provides
    UserDao providesUserDao(UserDatabase userDatabase) {
        return userDatabase.getUserDao();
    }

    @Singleton
    @Provides
    HospitalDao providesHospitalDao(UserDatabase userDatabase) {
        return userDatabase.getHospitalDao();
    }

    @Singleton
    @Provides
    HospitalDeviceDao providesHospitalDeviceDao(UserDatabase userDatabase) {
        return userDatabase.getHospitalDeviceDao();
    }

    @Singleton
    @Provides
    ReportDao providesReportDao(UserDatabase userDatabase) {
        return userDatabase.getReportDao();
    }

    @Singleton
    @Provides
    UserRepository userRepository(UserDao userDao, Context context) {
        return new UserRepository(userDao, context);
    }

    @Singleton
    @Provides
    HospitalRepository hospitalRepository(HospitalDao hospitalDao, Context context) {
        return new HospitalRepository(hospitalDao, context);
    }
}
