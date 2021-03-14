package ngo.teog.swift.gui.deviceInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class DeviceInfoViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private final MutableLiveData<DeviceInfo> liveDeviceInfo = new MutableLiveData<>();
    private int userId;
    private int deviceId;

    public void init(int userId, int deviceId) {
        this.userId = userId;
        this.deviceId = deviceId;

        new Thread(new LoadRunner()).start();
    }

    @Inject
    public DeviceInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void updateDevice(HospitalDevice device, int userId) {
        hospitalRepo.updateDevice(device, userId);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return liveDeviceInfo;
    }

    public void refreshDevice() {
        new Thread(new LoadRunner()).start();
    }

    private class LoadRunner implements Runnable {
        @Override
        public void run() {
            DeviceInfo deviceInfo = hospitalRepo.getDevice(userId, deviceId);
            liveDeviceInfo.postValue(deviceInfo);
        }
    }
}
