package ngo.teog.swift.helpers.data;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity2;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity3;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.gui.hospital.AdvancedHospitalActivity;
import ngo.teog.swift.gui.hospital.HospitalActivity;
import ngo.teog.swift.gui.main.BarcodeFragment;
import ngo.teog.swift.gui.main.CalendarFragment;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.gui.main.TodoFragment;
import ngo.teog.swift.gui.maintenance.SearchActivity3;
import ngo.teog.swift.gui.reportCreation.ReportCreationActivity;
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.gui.userProfile.UserProfileActivity;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(UserProfileActivity target);
    void inject(HospitalActivity target);
    void inject(AdvancedHospitalActivity target);
    void inject(TodoFragment target);
    void inject(UserInfoActivity target);
    void inject(DeviceInfoActivity target);
    void inject(BarcodeFragment target);
    void inject(NewDeviceActivity3 target);
    void inject(NewDeviceActivity2 target);
    void inject(ReportCreationActivity target);
    void inject(SearchActivity3 target);
    void inject(MainActivity target);
    void inject(ReportInfoActivity target);
    void inject(CalendarFragment target);

    HospitalDatabase userDatabase();

    HospitalRepository userRepository();

    Application application();
}