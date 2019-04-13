package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ngo.teog.swift.gui.hospital.HospitalViewModel;
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
}
