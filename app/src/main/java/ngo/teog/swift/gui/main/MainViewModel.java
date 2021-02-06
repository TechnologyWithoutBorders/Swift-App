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
        hospital = hospitalRepo.getUserHospital(userId);
        deviceInfos = hospitalRepo.getHospitalDevices(userId, false);
    }

    public LiveData<Hospital> getUserHospital() {
        return hospital;
    }

    public LiveData<DeviceInfo> getDeviceInfo(int deviceId) {
        return hospitalRepo.getDevice(userId, deviceId);
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }

    public void refreshHospital(int userId) {
        hospitalRepo.refreshUserHospital(userId);
    }
}
