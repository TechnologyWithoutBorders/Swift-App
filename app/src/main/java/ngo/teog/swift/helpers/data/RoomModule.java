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
    HospitalDao providesHospitalDao(UserDatabase userDatabase) {
        return userDatabase.getHospitalDao();
    }

    @Singleton
    @Provides
    UserRepository userRepository(HospitalDao hospitalDao, Context context) {
        return new UserRepository(hospitalDao, context);
    }
}
