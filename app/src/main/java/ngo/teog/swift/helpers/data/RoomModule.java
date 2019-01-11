package ngo.teog.swift.helpers.data;

import android.app.Application;
import android.arch.persistence.room.Room;

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
    UserRepository userRepository(UserDao userDao) {
        return new UserRepository(userDao);
    }
}
