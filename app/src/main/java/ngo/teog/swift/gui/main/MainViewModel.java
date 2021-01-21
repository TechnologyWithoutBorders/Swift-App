package ngo.teog.swift.gui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class MainViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private final HospitalRepository hospitalRepo;

    @Inject
    public MainViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.getUserHospital(userId);
    }

    public LiveData<Hospital> getUserHospital() {
        return hospital;
    }
}
