package ngo.teog.swift.helpers.data;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import ngo.teog.swift.gui.UserProfileActivity;
import ngo.teog.swift.gui.main.MainActivity;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(UserProfileActivity mainActivity);

    UserDao userDao();

    UserDatabase userDatabase();

    UserRepository userRepository();

    Application application();

}