package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;

public class AdvancedHospitalViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;

    @Inject
    public AdvancedHospitalViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void refreshHospital(int userId) {
        hospitalRepo.refreshUserHospital(userId);
    }
}