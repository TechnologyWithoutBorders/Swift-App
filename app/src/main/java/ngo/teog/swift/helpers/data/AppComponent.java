package ngo.teog.swift.helpers.data;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.gui.hospital.HospitalActivity;
import ngo.teog.swift.gui.main.TodoFragment;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoViewModel;
import ngo.teog.swift.gui.userProfile.UserProfileActivity;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(UserProfileActivity target);
    void inject(HospitalActivity target);
    void inject(TodoFragment target);
    void inject(UserInfoActivity target);
    void inject(DeviceInfoActivity target);

    HospitalDatabase userDatabase();

    HospitalRepository userRepository();

    Application application();
}