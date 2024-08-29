package ngo.teog.swift.gui.userProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserInfo;

public class UserProfileViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;
    private LiveData<UserInfo> userProfile;
    private LiveData<List<DeviceInfo>> devices;

    @Inject
    public UserProfileViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.userProfile != null) {
            return;
        }

        userProfile = hospitalRepo.loadUserProfileInfo(userId, true);
        devices = hospitalRepo.loadHospitalDevices(userId, false);
    }

    public LiveData<UserInfo> getUserProfile() {
        return userProfile;
    }

    public LiveData<List<DeviceInfo>> getHospitalDevices() {
        return devices;
    }

    public void updateUser(User user) {
        hospitalRepo.updateUser(user);
    }
}
