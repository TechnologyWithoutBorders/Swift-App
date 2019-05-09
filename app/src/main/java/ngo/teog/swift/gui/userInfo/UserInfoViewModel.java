package ngo.teog.swift.gui.userInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserInfo;

public class UserInfoViewModel extends ViewModel {
    private LiveData<UserInfo> userInfo;
    private HospitalRepository hospitalRepo;

    @Inject
    public UserInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int myId, int userId) {
        if(this.userInfo != null) {
            return;
        }

        userInfo = hospitalRepo.getUserInfo(myId, userId);
    }

    public LiveData<UserInfo> getUserInfo() {
        return userInfo;
    }
}
