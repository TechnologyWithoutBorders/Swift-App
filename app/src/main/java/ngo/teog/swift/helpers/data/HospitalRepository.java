package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Debugging;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.ResponseException;
import ngo.teog.swift.helpers.ResponseParser;
import ngo.teog.swift.helpers.SwiftResponse;
import ngo.teog.swift.helpers.filters.UserFilter;

@Singleton
public class HospitalRepository {

    private final HospitalDao hospitalDao;
    private final Context context;
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Inject
    public HospitalRepository(HospitalDao hospitalDao, Context context) {
        this.hospitalDao = hospitalDao;
        this.context = context;
    }

    public LiveData<User> getUser(int userId) {
        //TODO aktuell beschränken wir uns auf unser eigenes Krankenhaus
        refreshUserHospital(userId);

        return hospitalDao.loadUser(userId);
    }

    public LiveData<Hospital> getHospital(int hospitalId) {
        //refreshHospital(hospitalId);

        return hospitalDao.loadUserHospital(hospitalId);
    }

    public LiveData<Hospital> getUserHospital(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserHospital(userId);
    }

    public LiveData<List<User>> getUserColleagues(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserColleagues(userId);
    }

    public LiveData<DeviceInfo> getDevice(int deviceId) {
        //refreshUserHospital(userId);

        return hospitalDao.loadDevice(deviceId);
    }

    public LiveData<List<DeviceInfo>> getHospitalDevices(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadHospitalDevices(userId);
    }

    public void updateUser(User user) {//TODO vielleicht sogar mit refreshUsers() in eine gemeinsame syncWithServer()-Methode zusammenführen
        executor.execute(() -> {
            hospitalDao.save(user);

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

            RequestFactory.DefaultRequest request = new RequestFactory().createProfileUpdateRequest(context, null, null, user);

            queue.add(request);
        });
    }

    private void refreshUserHospital(int userId) {
        executor.execute(() -> {
            //TODO check if user data has been fetched recently

            //refresh
            if(this.checkForInternetConnection()) {
                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

                HospitalRequest hospitalRequest = createHospitalRequest(context, userId, executor);

                queue.add(hospitalRequest);
            }
        });
    }

    /**
     * Aktualisiert die Benutzer des eigenen Krankenhauses und zusätzlich den angefragter Benutzer
     * @param context
     * @param executor
     * @return
     */
    private HospitalRequest createHospitalRequest(Context context, int userID, ExecutorService executor) {
        final String url = Defaults.BASE_URL + Defaults.HOSPITALS_URL;

        //TODO man könnte auch immer das last_update mitschicken und nur aktualisieren, wenn es neuere Daten gibt -> Netzwerkaufwand minimieren
        //TODO aber eigentlich übertragen wir ja auch jetzt schon ziemlich wenige Daten
        Map<String, String> params = generateParameterMap(context, "fetch_hospital_info", true);
        params.put(UserFilter.ID, Integer.toString(userID));

        JSONObject request = new JSONObject(params);

        return new HospitalRequest(context, url, request, executor);
    }

    private class HospitalRequest extends JsonObjectRequest {

        private HospitalRequest(final Context context, final String url, JSONObject request, ExecutorService executor) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    executor.execute(() -> {
                        try {
                            HospitalInfo hospitalInfo = new ResponseParser().parseHospital(response);

                            //TODO prüfen, ob es lokal neuere gibt und wenn ja auf den Server pushen

                            Hospital hospital = new Hospital(hospitalInfo.getId(), hospitalInfo.getName(), hospitalInfo.getLocation(), hospitalInfo.getLastUpdate());

                            hospitalDao.save(hospital);

                            for(User user : hospitalInfo.getUsers()) {
                                hospitalDao.save(user);
                            }

                            for(DeviceInfo deviceInfo : hospitalInfo.getDevices()) {
                                hospitalDao.save(deviceInfo.getDevice());
                                hospitalDao.save(deviceInfo.getReports().get(0));
                            }
                        } catch(Exception e) {
                            Log.e("SAVE_USER", e.getMessage(), e);
                            Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private HashMap<String, String> generateParameterMap(Context context, String action, boolean userValidation) {
        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("action", action);
        parameterMap.put("country", preferences.getString(Defaults.COUNTRY_PREFERENCE, null));

        if(userValidation) {
            parameterMap.put("validation_id", Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
            parameterMap.put("validation_pw", preferences.getString(Defaults.PW_PREFERENCE, null));
        }

        return parameterMap;
    }

    /*Constraints updateConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data inputData = new Data.Builder()
                    .putInt("id", id)
                    .build();

            //TODO hier funktioniert die Dependency Injection nicht, der Workaround ist aber ziemlich umständlich
            OneTimeWorkRequest updateWork = new OneTimeWorkRequest.Builder(UpdateWorker.class)
                    .addTag("update_profile")
                    .setConstraints(updateConstraints)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance().enqueue(updateWork);*/

    /*private class UpdateWorker extends Worker {

        private Context context;

        public UpdateWorker(Context context, WorkerParameters params) {
            super(context, params);

            this.context = context;
        }

        @NonNull
        @Override
        public Worker.Result doWork() {
            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

            UserListRequest userListRequest = createUserRequest(context, this.getInputData().getInt("id", -1), executor);

            queue.add(userListRequest);

            return Result.success();
        }
    }*/

    public boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
