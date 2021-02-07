package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.export.HospitalDump;

public class PortationViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<HospitalDump> hospital;

    @Inject
    public PortationViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.loadHospitalDump(userId);
    }

    public LiveData<HospitalDump> getHospitalDump() {
        return hospital;
    }
}