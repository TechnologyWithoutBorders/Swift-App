package ngo.teog.swift.gui.userProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserInfo;
import ngo.teog.swift.helpers.export.HospitalDump;

public class UserProfileViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<UserInfo> userProfile;
    private LiveData<HospitalDump> hospital;

    @Inject
    public UserProfileViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.userProfile != null) {
            return;
        }

        userProfile = hospitalRepo.loadUserProfileInfo(userId, true);
        hospital = hospitalRepo.loadHospitalDump(userId);
    }

    public LiveData<UserInfo> getUserProfile() {
        return userProfile;
    }

    public LiveData<HospitalDump> getHospitalDump() {
        return hospital;
    }

    public void updateUser(User user) {
        hospitalRepo.updateUser(user);
    }
}
