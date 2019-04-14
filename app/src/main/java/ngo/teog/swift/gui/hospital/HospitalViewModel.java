package ngo.teog.swift.gui.hospital;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class HospitalViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private LiveData<List<User>> users;
    private LiveData<DeviceInfo> deviceInfos;
    private HospitalRepository userRepo;

    @Inject
    public HospitalViewModel(HospitalRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        hospital = userRepo.getUserHospital(userId);
        users = userRepo.getUserColleagues(userId);
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }
}
