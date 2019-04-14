package ngo.teog.swift.gui.hospital;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class HospitalViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
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
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }
}
