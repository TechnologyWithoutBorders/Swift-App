package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(() -> {
            // Check if user data was fetched recently
            boolean userExists = (userDao.hasUser(id, 0) != 0);
            if (!userExists) {
                //TODO Refresh the data.
                User user = new User(1, "12345678", "julian.deyerler@teog.de", "Julian Deyerler", 0, "Developer");

                //TODO Check for errors here.

                // Updates the database. The LiveData object automatically
                // refreshes, so we don't need to do anything else here.
                userDao.save(user);

                Log.d("SAVE_USER", "saved");
            }
        });

        executor.shutdown();
    }

}
