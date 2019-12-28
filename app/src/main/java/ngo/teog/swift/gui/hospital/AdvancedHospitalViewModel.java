package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.User;

public class AdvancedHospitalViewModel extends ViewModel {
    private LiveData<Hospital> hospital;
    private LiveData<List<User>> users;
    private HospitalRepository hospitalRepo;

    @Inject
    public AdvancedHospitalViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.getUserHospital(userId);
        users = hospitalRepo.getUserColleagues(userId);
    }

    public LiveData<Hospital> getHospital() {
        return hospital;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void createUser(User user, int userId) {
        hospitalRepo.createUser(user, userId);
    }
}