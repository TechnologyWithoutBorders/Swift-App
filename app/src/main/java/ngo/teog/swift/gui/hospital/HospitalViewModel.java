package ngo.teog.swift.gui.hospital;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.data.UserRepository;

public class HospitalViewModel extends ViewModel {
    private LiveData<HospitalInfo> hospital;
    private UserRepository userRepo;

    @Inject
    public HospitalViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int userId) {
        hospital = userRepo.getHospitalInfo(userId);
    }

    public LiveData<HospitalInfo> getHospital() {
        return hospital;
    }
}
