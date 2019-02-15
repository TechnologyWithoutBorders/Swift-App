package ngo.teog.swift.gui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import ngo.teog.swift.helpers.data.UserRepository;
import ngo.teog.swift.helpers.data.User;

public class LoginViewModel extends ViewModel {
    private LiveData<User> user;
    private UserRepository userRepo;

    @Inject
    public LoginViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void init(int id) {
        if (this.user != null) {
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
