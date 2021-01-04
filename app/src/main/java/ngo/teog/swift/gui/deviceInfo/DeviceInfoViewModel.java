package ngo.teog.swift.gui.deviceInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class DeviceInfoViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<DeviceInfo> deviceInfo;

    public void init(int userId, int deviceId) {
        if(this.deviceInfo != null) {
            return;
        }

        deviceInfo = hospitalRepo.getDevice(userId, deviceId);
    }

    @Inject
    public DeviceInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void updateDevice(HospitalDevice device, int userId) {
        hospitalRepo.updateDevice(device, userId);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return deviceInfo;
    }

    public void refreshHospital(int userId) {
        hospitalRepo.refreshUserHospital(userId);
    }
}
