package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class HospitalViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    //no mutable live data needed as the user list won't change that much
    private LiveData<List<User>> users;
    private LiveData<List<DeviceInfo>> deviceInfos;
    private final HospitalRepository hospitalRepo;

    @Inject
    public HospitalViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.loadUserHospital(userId, true);
        users = hospitalRepo.loadUserColleagues(userId, false);
        deviceInfos = hospitalRepo.loadHospitalDevices(userId, false);
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }
}
