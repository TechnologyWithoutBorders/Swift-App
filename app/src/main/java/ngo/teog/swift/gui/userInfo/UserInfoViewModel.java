package ngo.teog.swift.gui.userInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.UserInfo;

public class UserInfoViewModel extends ViewModel {
    private LiveData<UserInfo> userInfo;
    private final HospitalRepository hospitalRepo;

    @Inject
    public UserInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.userInfo != null) {
            return;
        }

        userInfo = hospitalRepo.loadUserInfo(userId, true);
    }

    public LiveData<UserInfo> getUserInfo() {
        return userInfo;
    }
}
