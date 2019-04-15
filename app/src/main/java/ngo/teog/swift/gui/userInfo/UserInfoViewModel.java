package ngo.teog.swift.gui.userInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class UserInfoViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private HospitalRepository hospitalRepo;

    @Inject
    public UserInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int id) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.getUserHospital(id);
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }
}
