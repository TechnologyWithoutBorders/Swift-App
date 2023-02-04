package ngo.teog.swift.gui.reportInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Observable;
import ngo.teog.swift.helpers.data.Report;

public class ReportInfoViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<Observable> observable;
    private final MutableLiveData<DeviceInfo> liveDeviceInfo = new MutableLiveData<>();
    private int userId;
    private int deviceId;

    public LiveData<Observable> init(int userId, int deviceId) {
        if(this.observable != null) {
            return observable;
        }

        this.userId = userId;
        this.deviceId = deviceId;

        observable = hospitalRepo.loadObservable(Defaults.SYNC_OBSERVABLE);

        new Thread(new DeviceInfoLoadRunner(userId, deviceId)).start();

        return observable;
    }

    @Inject
    public ReportInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return liveDeviceInfo;
    }

    public void refreshDevice() {
        new Thread(new DeviceInfoLoadRunner(this.userId, this.deviceId)).start();
    }

    public void createReport(Report report, int userId) {
        hospitalRepo.createReport(report, userId);
    }

    private class DeviceInfoLoadRunner implements Runnable {
        private final int userId;
        private final int deviceId;

        public DeviceInfoLoadRunner(int userId, int deviceId) {
            this.userId = userId;
            this.deviceId = deviceId;
        }

        @Override
        public void run() {
            DeviceInfo deviceInfo = hospitalRepo.getDevice(userId, deviceId);
            liveDeviceInfo.postValue(deviceInfo);
        }
    }
}
