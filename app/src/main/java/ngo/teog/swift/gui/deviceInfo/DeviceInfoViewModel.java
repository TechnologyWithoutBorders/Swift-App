package ngo.teog.swift.gui.deviceInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Observable;
import ngo.teog.swift.helpers.data.OrganizationalUnit;

public class DeviceInfoViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<Observable> observable;
    private LiveData<List<OrganizationalUnit>> orgUnits;
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
        orgUnits = hospitalRepo.loadOrgUnits(userId);

        new Thread(new DeviceInfoLoadRunner(userId, deviceId)).start();

        return observable;
    }

    @Inject
    public DeviceInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void updateDevice(HospitalDevice device) {
        hospitalRepo.updateDevice(device, this.userId);
    }

    public LiveData<DeviceInfo> getDeviceInfo() {
        return liveDeviceInfo;
    }

    public LiveData<List<OrganizationalUnit>> getOrgUnits() {
        return orgUnits;
    }

    public void refreshDevice() {
        new Thread(new DeviceInfoLoadRunner(this.userId, this.deviceId)).start();
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
