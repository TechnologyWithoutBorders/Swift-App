package ngo.teog.swift.gui.deviceCreation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class NewDeviceViewModel extends ViewModel {
    private LiveData<DeviceInfo> device;
    private LiveData<User> user;
    private HospitalRepository hospitalRepo;

    @Inject
    public NewDeviceViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId, int deviceId) {
        if (this.device != null) {
            return;
        }

        user = hospitalRepo.getUser(userId);
        device = hospitalRepo.getDevice(deviceId);
    }

    public void createDevice(HospitalDevice device, int userId) {
        hospitalRepo.createDevice(device, userId);
    }

    public LiveData<DeviceInfo> getDevice() {
        return device;
    }

    public LiveData<User> getUser() { return user; }
}
