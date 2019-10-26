package ngo.teog.swift.gui.userProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserProfileInfo;

public class UserProfileViewModel extends ViewModel {
    private LiveData<UserProfileInfo> userProfile;
    private HospitalRepository hospitalRepo;

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

    public LiveData<UserProfileInfo> getUserProfile() {
        return userProfile;
    }

    public void updateUser(User user) {
        hospitalRepo.updateUser(user);
    }
}
