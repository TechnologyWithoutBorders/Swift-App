package ngo.teog.swift.gui.maintenance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class MaintenanceViewModel extends ViewModel {
    private LiveData<List<DeviceInfo>> deviceInfos;
    private HospitalRepository hospitalRepo;

    @Inject
    public MaintenanceViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.deviceInfos != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        deviceInfos = hospitalRepo.getHospitalDevices(userId);
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }
}
