package ngo.teog.swift.gui.reportInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.ReportInfo;

public class ReportInfoViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<DeviceInfo> deviceInfo;

    public void init(int userId, int deviceId) {
        if(this.deviceInfo != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        deviceInfo = hospitalRepo.loadDevice(userId, deviceId, true);
    }

    @Inject
    public ReportInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return deviceInfo;
    }
}
