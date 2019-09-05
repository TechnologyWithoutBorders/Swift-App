package ngo.teog.swift.helpers.data;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private HospitalDatabase hospitalDatabase;

    public RoomModule(Application mApplication) {
        hospitalDatabase = Room.databaseBuilder(mApplication, HospitalDatabase.class, "hospital-db").build();
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
}
