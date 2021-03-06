package ngo.teog.swift.gui.deviceCreation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class NewDeviceViewModel2 extends ViewModel {
    private LiveData<List<DeviceInfo>> deviceInfos;
    private final HospitalRepository hospitalRepo;

    @Inject
    public NewDeviceViewModel2(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.deviceInfos != null) {
            return;
        }

        deviceInfos = hospitalRepo.loadHospitalDevices(userId, true);
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }
}