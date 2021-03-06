package ngo.teog.swift.gui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class MainViewModel extends ViewModel {
    private int userId;
    private LiveData<Hospital> hospital;
    private LiveData<List<DeviceInfo>> deviceInfos;
    private final HospitalRepository hospitalRepo;

    @Inject
    public MainViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        this.userId = userId;
        hospital = hospitalRepo.loadUserHospital(userId, true);
        deviceInfos = hospitalRepo.loadHospitalDevices(userId, false);
    }

    public LiveData<Hospital> getUserHospital() {
        return hospital;
    }

    /**
     * Retrieves device info from the database. As this method is used for finding a device
     * via barcode, no synchronization with the server will be invoked.
     * @param deviceId device ID
     * @return device info
     */
    public LiveData<DeviceInfo> getDeviceInfo(int deviceId) {
        return hospitalRepo.loadDevice(userId, deviceId, false);
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }

    public void refreshHospital(int userId) {
        hospitalRepo.refreshUserHospital(userId);
    }
}
