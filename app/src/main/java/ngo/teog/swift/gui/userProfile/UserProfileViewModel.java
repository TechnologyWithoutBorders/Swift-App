package ngo.teog.swift.gui.userProfile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.UserRepository;
import ngo.teog.swift.helpers.data.User;

public class UserProfileViewModel extends ViewModel {
    private LiveData<User> user;
    private LiveData<Hospital> hospital;
    private UserRepository userRepo;

    @Inject
    public UserProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int id) {
        if(this.user != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        user = userRepo.getUser(id);
    }

    public LiveData<User> getUser() {
        return user;
    }

    //TODO keine Ahnung, ob man das so macht
    public void updateUser(User user) {
        userRepo.updateUser(user);
    }
}
