package ngo.teog.swift.helpers.data;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ngo.teog.swift.gui.ImageCaptureViewModel;
import ngo.teog.swift.gui.deviceCreation.NewDeviceViewModel3;
import ngo.teog.swift.gui.deviceCreation.NewDeviceViewModel2;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoViewModel;
import ngo.teog.swift.gui.hospital.HospitalViewModel;
import ngo.teog.swift.gui.hospital.PortationViewModel;
import ngo.teog.swift.gui.main.MainViewModel;
import ngo.teog.swift.gui.reportInfo.ReportInfoViewModel;
import ngo.teog.swift.gui.userInfo.UserInfoViewModel;
import ngo.teog.swift.gui.userProfile.UserProfileViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
    //You are able to declare ViewModelProvider.Factory dependency in another module. For example in ApplicationModule.

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    public abstract ViewModel userProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HospitalViewModel.class)
    public abstract ViewModel hospitalViewModel(HospitalViewModel hospitalViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserInfoViewModel.class)
    public abstract ViewModel userInfoViewModel(UserInfoViewModel userInfoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeviceInfoViewModel.class)
    public abstract ViewModel deviceInfoViewModel(DeviceInfoViewModel deviceInfoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NewDeviceViewModel3.class)
    public abstract ViewModel newDeviceViewModel3(NewDeviceViewModel3 newDeviceViewModel3);

    @Binds
    @IntoMap
    @ViewModelKey(ReportInfoViewModel.class)
    public abstract ViewModel reportInfoViewModel(ReportInfoViewModel reportInfoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NewDeviceViewModel2.class)
    public abstract ViewModel newDeviceViewModel2(NewDeviceViewModel2 newDeviceViewModel2);

    @Binds
    @IntoMap
    @ViewModelKey(PortationViewModel.class)
    public abstract ViewModel portationViewModel(PortationViewModel portationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel mainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageCaptureViewModel.class)
    public abstract ViewModel imageCaptureViewModel(ImageCaptureViewModel imageCaptureViewModel);
}
