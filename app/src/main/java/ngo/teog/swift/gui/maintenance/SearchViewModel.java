package ngo.teog.swift.gui.maintenance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.UserInfo;

public class SearchViewModel extends ViewModel {
    private LiveData<UserInfo> userInfo;
    private HospitalRepository hospitalRepo;

    @Inject
    public SearchViewModel(HospitalRepository hospitalRepo) {
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
