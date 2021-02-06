package ngo.teog.swift.gui.deviceCreation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class NewDeviceViewModel extends ViewModel {
    private LiveData<DeviceInfo> device;
    private LiveData<User> user;
    private final HospitalRepository hospitalRepo;

    @Inject
    public NewDeviceViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId, int deviceId) {
        if (this.device != null) {
            return;
        }

        user = hospitalRepo.getUser(userId);
        device = hospitalRepo.getDevice(userId, deviceId, false);
    }

    public void createDevice(HospitalDevice device, int userId) {
        hospitalRepo.createDevice(device, userId);
    }

    public LiveData<DeviceInfo> getDevice() {
        return device;
    }

    public LiveData<User> getUser() { return user; }
}
