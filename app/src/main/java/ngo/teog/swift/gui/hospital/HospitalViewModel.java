package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Observable;
import ngo.teog.swift.helpers.data.User;

public class HospitalViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private LiveData<Observable> observable;
    //no mutable live data needed as the user list won't change that much
    private LiveData<List<User>> validUsers;
    private final MutableLiveData<List<DeviceInfo>> liveDeviceInfos = new MutableLiveData<>();
    private final HospitalRepository hospitalRepo;
    private int userId;

    @Inject
    public HospitalViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public LiveData<Observable> init(int userId) {
        if(this.observable != null) {
            return observable;
        }

        this.userId = userId;

        observable = hospitalRepo.loadObservable(Defaults.SYNC_OBSERVABLE);
        hospital = hospitalRepo.loadUserHospital(userId, true);
        validUsers = hospitalRepo.loadValidUserColleagues(userId, false);

        new Thread(new DeviceInfoLoadRunner(userId)).start();

        return observable;
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }

    public LiveData<List<User>> getValidUsers() {
        return validUsers;
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return liveDeviceInfos;
    }

    public void refreshDeviceInfos() {
        new Thread(new DeviceInfoLoadRunner(this.userId)).start();
    }

    private class DeviceInfoLoadRunner implements Runnable {
        private final int userId;

        public DeviceInfoLoadRunner(int userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            List<DeviceInfo> deviceInfos = hospitalRepo.getHospitalDevices(userId);
            liveDeviceInfos.postValue(deviceInfos);
        }
    }
}
