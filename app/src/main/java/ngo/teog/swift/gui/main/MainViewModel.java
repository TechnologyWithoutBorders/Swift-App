package ngo.teog.swift.gui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Observable;

public class MainViewModel extends ViewModel {
    private int userId;
    private LiveData<Observable> observable;
    private LiveData<Hospital> hospital;
    private final MutableLiveData<List<DeviceInfo>> liveDeviceInfos = new MutableLiveData<>();
    private final HospitalRepository hospitalRepo;

    @Inject
    public MainViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public LiveData<Observable> init(int userId) {
        if(this.observable != null) {
            return observable;
        }

        this.userId = userId;

        observable = hospitalRepo.loadObservable(1);
        hospital = hospitalRepo.loadUserHospital(userId, true);

        new Thread(new DeviceInfosLoadRunner(userId)).start();

        return observable;
    }

    public LiveData<Hospital> getUserHospital() {
        return hospital;
    }

    public void refreshHospital() {
        hospitalRepo.refreshUserHospital(this.userId);
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
        return liveDeviceInfos;
    }

    public void refreshDeviceInfos() {
        new Thread(new DeviceInfosLoadRunner(this.userId)).start();
    }

    private class DeviceInfosLoadRunner implements Runnable {
        private final int userId;

        public DeviceInfosLoadRunner(int userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            List<DeviceInfo> deviceInfos = hospitalRepo.getHospitalDevices(userId);
            liveDeviceInfos.postValue(deviceInfos);
        }
    }
}
