package ngo.teog.swift.gui.deviceInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class DeviceInfoViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;
    private LiveData<DeviceInfo> deviceInfo;

    public void init(int deviceId) {
        if(this.deviceInfo != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        deviceInfo = hospitalRepo.getDevice(deviceId);
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
}
