package ngo.teog.swift.helpers.data;

import android.app.Activity;
import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import ngo.teog.swift.gui.hospital.HospitalActivity;
import ngo.teog.swift.gui.login.LoginActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.gui.userProfile.UserProfileActivity;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(UserProfileActivity target);
    void inject(HospitalActivity target);

    UserDao userDao();

    UserDatabase userDatabase();

    UserRepository userRepository();

    HospitalRepository hospitalRepository();

    Application application();
}