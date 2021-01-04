package ngo.teog.swift.gui.userProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserInfo;

public class UserProfileViewModel extends ViewModel {
    private LiveData<UserInfo> userProfile;
    private final HospitalRepository hospitalRepo;

    @Inject
    public UserProfileViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.userProfile != null) {
            return;
        }

        userProfile = hospitalRepo.getUserProfileInfo(userId);
    }

    public LiveData<UserInfo> getUserProfile() {
        return userProfile;
    }

    public void updateUser(User user) {
        hospitalRepo.updateUser(user);
    }
}
