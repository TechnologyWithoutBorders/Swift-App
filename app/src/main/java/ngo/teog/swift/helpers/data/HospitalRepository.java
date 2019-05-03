package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.ResponseParser;
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

    public LiveData<UserProfileInfo> getUserProfileInfo(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserProfile(userId);
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

    public void updateUser(User user) {
        executor.execute(() -> {
            hospitalDao.save(user);

            refreshUserHospitalSync(user.getId());
        });
    }

    public void updateDevice(HospitalDevice device, int userId) {
        executor.execute(() -> {
            hospitalDao.save(device);

            refreshUserHospitalSync(userId);
        });
    }

    private void refreshUserHospitalSync(int userId) {
        //TODO check if user data has been fetched recently

        //refresh
        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

            HospitalRequest hospitalRequest = createHospitalRequest(context, userId, executor);

            queue.add(hospitalRequest);
        }
    }

    public void createDevice(HospitalDevice device, int userId) {
        executor.execute(() -> {
            Report creationReport = new Report(-1, userId, device.getId(), 0, 0, "device creation", device.getLastUpdate());

            hospitalDao.save(device);
            hospitalDao.save(creationReport);

            refreshUserHospitalSync(userId);
        });
    }

    public void createReport(Report report, int userId) {
        executor.execute(() -> {
            hospitalDao.save(report);

            refreshUserHospitalSync(userId);
        });
    }

    public void refreshUserHospital(int userId) {
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

        try {
            Gson gson = new Gson();

            //Der Server muss dann eventuelle Kollisionen bei den Reports ausgleichen
            Map<String, String> params = generateParameterMap(context, "sync_hospital_info", true);
            params.put(UserFilter.ID, Integer.toString(userID));

            JSONArray jsonHospitals = new JSONArray();
            JSONArray jsonDevices = new JSONArray();
            JSONArray jsonUsers = new JSONArray();
            JSONArray jsonReports = new JSONArray();

            SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            long lastUpdate = preferences.getLong(Defaults.LAST_SYNC_PREFERENCE, System.currentTimeMillis())/1000;

            Hospital hospital = hospitalDao.getUserHospital(userID);

            if(hospital != null && hospital.getLastUpdate() >= lastUpdate) {
                jsonHospitals.put(new JSONObject(gson.toJson(hospital)));
            }

            List<User> users = hospitalDao.getUserColleagues(userID);

            if(users != null) {
                for (User user : users) {
                    if (user.getLastUpdate() >= lastUpdate) {
                        jsonUsers.put(new JSONObject(gson.toJson(user)));
                    }
                }
            }

            List<DeviceInfo> deviceInfos = hospitalDao.getHospitalDevices(userID);

            if(deviceInfos != null) {
                for (DeviceInfo deviceInfo : deviceInfos) {
                    HospitalDevice device = deviceInfo.getDevice();

                    if (device.getLastUpdate() >= lastUpdate) {
                        jsonDevices.put(new JSONObject(gson.toJson(device)));
                    }

                    List<ReportInfo> reports = deviceInfo.getReports();

                    for(ReportInfo reportInfo : reports) {
                        Report report = reportInfo.getReport();

                        if(report.getCreated() >= lastUpdate) {
                            jsonReports.put(new JSONObject(gson.toJson(report)));
                        }
                    }
                }
            }

            JSONObject data = new JSONObject();

            JSONObject request = new JSONObject(params);

            data.put("hospitals", jsonHospitals);
            data.put("devices", jsonDevices);
            data.put("users", jsonUsers);
            data.put("reports", jsonReports);

            request.put("data", data);

            return new HospitalRequest(context, url, request, executor);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class HospitalRequest extends JsonObjectRequest {

        private HospitalRequest(final Context context, final String url, JSONObject request, ExecutorService executor) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    executor.execute(() -> {
                        try {
                            HospitalInfo hospitalInfo = new ResponseParser().parseHospital(response);

                            Hospital hospital = new Hospital(hospitalInfo.getId(), hospitalInfo.getName(), hospitalInfo.getLocation(), hospitalInfo.getLastUpdate());

                            hospitalDao.save(hospital);

                            for(User user : hospitalInfo.getUsers()) {
                                hospitalDao.save(user);
                            }

                            for(DeviceInfo deviceInfo : hospitalInfo.getDevices()) {
                                hospitalDao.save(deviceInfo.getDevice());

                                List<ReportInfo> reportInfos = deviceInfo.getReports();

                                for(ReportInfo reportInfo : reportInfos) {
                                    Report report = reportInfo.getReport();

                                    hospitalDao.save(report);
                                }
                            }

                            SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong(Defaults.LAST_SYNC_PREFERENCE, System.currentTimeMillis());
                            editor.apply();
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

    private boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
