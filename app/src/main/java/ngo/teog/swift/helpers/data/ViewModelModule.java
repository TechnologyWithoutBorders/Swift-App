package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ngo.teog.swift.gui.deviceCreation.NewDeviceViewModel;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoViewModel;
import ngo.teog.swift.gui.hospital.HospitalViewModel;
import ngo.teog.swift.gui.main.BarcodeViewModel;
import ngo.teog.swift.gui.main.TodoViewModel;
import ngo.teog.swift.gui.reportCreation.ReportCreationViewModel;
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
    @ViewModelKey(TodoViewModel.class)
    public abstract ViewModel todoViewModel(TodoViewModel todoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BarcodeViewModel.class)
    public abstract ViewModel barcodeViewModel(BarcodeViewModel barcodeViewModel);

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
    @ViewModelKey(NewDeviceViewModel.class)
    public abstract ViewModel newDeviceViewModel(NewDeviceViewModel newDeviceViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ReportCreationViewModel.class)
    public abstract ViewModel reportCreationViewModel(ReportCreationViewModel reportCreationViewModel);
}
