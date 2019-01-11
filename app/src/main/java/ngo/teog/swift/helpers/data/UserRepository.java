package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserDao;

public class UserRepository {

    private final UserDao userDao;
    private Executor executor;

    @Inject
    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
        this.executor = executor;
    }

    public LiveData<User> getUser(int id) {
        refreshUser(id);
        // Returns a LiveData object directly from the database.
        return userDao.load(id);
    }

    private void refreshUser(final int id) {
        // Runs in a background thread.
        //executor.execute(() -> {
            // Check if user data was fetched recently
            boolean userExists = (userDao.hasUser(id, 0) != 0);
            if (!userExists) {
                //TODO Refresh the data.
                User user = new User(0, "12345678", "julian.deyerler@teog.de", "Julian Deyerler", 0, "Developer");

                //TODO Check for errors here.

                // Updates the database. The LiveData object automatically
                // refreshes, so we don't need to do anything else here.
                userDao.save(user);
            }
        //});
    }

}
