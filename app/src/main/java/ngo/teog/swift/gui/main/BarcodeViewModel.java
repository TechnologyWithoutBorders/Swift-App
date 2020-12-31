package ngo.teog.swift.gui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class BarcodeViewModel extends ViewModel {
    private LiveData<DeviceInfo> deviceInfo;
    private final HospitalRepository hospitalRepo;

    @Inject
    public BarcodeViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId, int deviceId) {
        if(this.deviceInfo != null) {
            return;
        }

        deviceInfo = hospitalRepo.getDevice(userId, deviceId);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return deviceInfo;
    }
}
