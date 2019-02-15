package ngo.teog.swift.gui.deviceInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.UserRepository;
import ngo.teog.swift.helpers.data.User;

public class DeviceInfoViewModel extends ViewModel {
    private LiveData<HospitalDevice> device;
    private UserRepository userRepo;

    @Inject
    public DeviceInfoViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int id) {
        if (this.device != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }
        //device = userRepo.getDevice(id);
    }

    public LiveData<HospitalDevice> getDevice() {
        return device;
    }

    //TODO keine Ahnung, ob man das so macht
    public void updateUser(User user) {
        userRepo.updateUser(user);
    }
}
