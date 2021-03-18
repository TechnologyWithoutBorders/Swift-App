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

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        this.userId = userId;
        observable = hospitalRepo.loadObservable(1);
        hospital = hospitalRepo.loadUserHospital(userId, true);

        new Thread(new LoadRunner()).start();
    }

    public LiveData<Hospital> getUserHospital() {
        return hospital;
    }

    public LiveData<Observable> getUpdateIndicator() { return observable; }

    public void refreshHospital() {
        hospitalRepo.refreshUserHospital(userId);
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
        new Thread(new LoadRunner()).start();
    }

    private class LoadRunner implements Runnable {
        @Override
        public void run() {
            List<DeviceInfo> deviceInfos = hospitalRepo.getHospitalDevices(userId);
            liveDeviceInfos.postValue(deviceInfos);
        }
    }
}
