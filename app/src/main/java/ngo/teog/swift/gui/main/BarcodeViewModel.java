package ngo.teog.swift.gui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class BarcodeViewModel extends ViewModel {
    private LiveData<DeviceInfo> deviceInfo;
    private HospitalRepository hospitalRepo;

    @Inject
    public BarcodeViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int deviceId) {
        if(this.deviceInfo != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        deviceInfo = hospitalRepo.getDevice(deviceId);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return deviceInfo;
    }
}
