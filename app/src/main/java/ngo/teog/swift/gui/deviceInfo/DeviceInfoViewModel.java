package ngo.teog.swift.gui.deviceInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class DeviceInfoViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private HospitalRepository hospitalRepo;

    @Inject
    public DeviceInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int hospitalId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.getHospital(hospitalId);
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }
}
