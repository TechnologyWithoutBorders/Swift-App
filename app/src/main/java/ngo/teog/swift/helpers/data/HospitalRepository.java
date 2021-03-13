package ngo.teog.swift.helpers.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.communication.DataAction;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.communication.ResponseParser;
import ngo.teog.swift.helpers.export.HospitalDump;

/**
 * Provides access to relevant hospital data. Acts as the single source of truth and manages the various data sources and their synchronisation in the background.
 * @author nitelow
 */
@Singleton
public class HospitalRepository {

    private final HospitalDao hospitalDao;
    private final Context context;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Inject
    public HospitalRepository(HospitalDao hospitalDao, Context context) {
        this.hospitalDao = hospitalDao;
        this.context = context;
    }

    /**
     * Returns an <b>asynchronically</b> retrieved dump of an entire hospital.
     * @param userId User associated with specific hospital
     * @return hospital dump
     */
    public LiveData<HospitalDump> loadHospitalDump(int userId) {
        return hospitalDao.loadHospitalDump(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved user.
     * @param userId User
     * @param sync determines whether database should be synchronized with the server in the background
     * @return user
     */
    public LiveData<User> loadUser(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadUser(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved hospital.
     * @param userId User associated with specific hospital
     * @param sync determines whether database should be synchronized with the server in the background
     * @return hospital
     */
    public LiveData<Hospital> loadUserHospital(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadUserHospital(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved user.
     * @param userId User
     * @param sync determines whether database should be synchronized with the server in the background
     * @return information about user
     */
    public LiveData<UserInfo> loadUserProfileInfo(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadUserInfo(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved list of users associated with the hospital of the given user.
     * This list includes the given user itself.
     * @param userId User associated with specific hospital
     * @param sync determines whether database should be synchronized with the server in the background
     * @return Users associated with hospital of given user
     */
    public LiveData<List<User>> loadUserColleagues(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadUserColleagues(userId);
    }

    /**
     * Returns a <b>synchronically</b> retrieved list of users associated with the hospital of the given user.
     * This list includes the given user itself.
     * @param userId User associated with specific hospital
     * @return Users associated with hospital of given user
     */
    public List<User> getUserColleagues(int userId) {
        return hospitalDao.getUserColleagues(userId);
    }

    /**
     * Returns a <b>synchronically</b> retrieved list of devices associated with the hospital of the given user.
     * @param userId User associated with specific hospital
     * @return Devices associated with hospital of given user
     */
    public List<DeviceInfo> getHospitalDevices(int userId) {
        return hospitalDao.getHospitalDevices(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved device.
     * @param userId User associated with specific hospital
     * @param deviceId device ID
     * @param sync determines whether database should be synchronized with the server in the background
     * @return information about device
     */
    public LiveData<DeviceInfo> loadDevice(int userId, int deviceId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadDevice(userId, deviceId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved list of devices associated with the hospital of the given user.
     * @param userId User associated with specific hospital
     * @param sync determines whether database should be synchronized with the server in the background
     * @return Devices associated with hospital of given user
     */
    public LiveData<List<DeviceInfo>> loadHospitalDevices(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadHospitalDevices(userId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved report.
     * @param userId User associated with specific hospital
     * @param deviceId device ID
     * @param reportId report ID
     * @param sync determines whether database should be synchronized with the server in the background
     * @return information about report
     */
    public LiveData<ReportInfo> loadReportInfo(int userId, int deviceId, int reportId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadReportInfo(userId, deviceId, reportId);
    }

    /**
     * Returns an <b>asynchronically</b> retrieved user.
     * @param userId User
     * @param sync determines whether database should be synchronized with the server in the background
     * @return information about user
     */
    public LiveData<UserInfo> loadUserInfo(int userId, boolean sync) {
        if(sync) {
            refreshUserHospital(userId);
        }

        return hospitalDao.loadUserInfo(userId);
    }

    public void updateUser(User user) {
        executor.execute(() -> {
            hospitalDao.save(user);

            refreshUserHospitalSync(user.getId());
        });
    }

    public void updateDevice(HospitalDevice device, int userId) {//TODO refresh hospital of device as it is not necessarily the same as that of the requesting user
        executor.execute(() -> {
            hospitalDao.save(device);

            refreshUserHospitalSync(userId);
        });
    }

    public void updateHospitalSync(Hospital hospital) {
        hospitalDao.save(hospital);
    }

    public void updateUserSync(User user) {
        hospitalDao.save(user);
    }

    public void updateDeviceSync(HospitalDevice device) {
        hospitalDao.save(device);
    }

    public void updateReportSync(Report report) {
        hospitalDao.save(report);
    }

    public void createDevice(HospitalDevice device, int userId) {
        executor.execute(() -> {
            Date lastUpdate = new Date();

            String creationText = context.getString(R.string.initial_report_text);

            Report creationReport = new Report(1, userId, creationText, device.getId(), device.getHospital(), 0, 0, creationText, lastUpdate);
            device.setLastUpdate(lastUpdate);

            hospitalDao.save(device);
            hospitalDao.save(creationReport);

            refreshUserHospitalSync(userId);
        });
    }

    public void createReport(Report report, int userId) {
        executor.execute(() -> {
            hospitalDao.addReport(report);

            refreshUserHospitalSync(userId);
        });
    }

    private void refreshUserHospitalSync(int userId) {//TODO is there any actual difference to refreshUserHospital()?
        //TODO check if user data has been fetched recently

        //refresh
        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

            HospitalRequest hospitalRequest = createHospitalRequest(context, userId, executor);

            if(hospitalRequest != null) {
                queue.add(hospitalRequest);
            }
        }
    }

    public void refreshUserHospital(int userId) {
        executor.execute(() -> {
            //TODO check if user data has been fetched recently

            //refresh
            if(this.checkForInternetConnection()) {
                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

                HospitalRequest hospitalRequest = createHospitalRequest(context, userId, executor);

                if(hospitalRequest != null) {
                    queue.add(hospitalRequest);
                }
            }
        });
    }

    private HospitalRequest createHospitalRequest(Context context, int userID, ExecutorService executor) {
        final String url = Defaults.BASE_URL + Defaults.HOSPITALS_URL;

        DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat(Defaults.DATETIME_PRECISE_PATTERN)
                    .registerTypeAdapter(Date.class, new UtcDateTypeAdapter())
                    .create();

            SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            long lastUpdate = preferences.getLong(Defaults.LAST_SYNC_PREFERENCE, 0);

            //Der Server muss dann eventuelle Kollisionen bei den Reports ausgleichen
            Map<String, String> params = RequestFactory.generateParameterMap(context, DataAction.SYNC_HOSPITAL_INFO, true);
            params.put(ResourceKeys.LAST_SYNC, dateFormat.format(new Date(lastUpdate)));

            JSONArray jsonDevices = new JSONArray();
            JSONArray jsonUsers = new JSONArray();
            JSONArray jsonReports = new JSONArray();

            //assure that no dataset with invalid timestamp is synchronized to server
            long now = new Date().getTime();

            List<User> users = HospitalRepository.this.getUserColleagues(userID);

            if(users != null) {
                for (User user : users) {
                    if(user.getLastUpdate().getTime() >= lastUpdate && user.getLastUpdate().getTime() <= now) {
                        jsonUsers.put(new JSONObject(gson.toJson(user)));
                    }
                }
            }

            List<DeviceInfo> deviceInfos = HospitalRepository.this.getHospitalDevices(userID);

            if(deviceInfos != null) {
                for (DeviceInfo deviceInfo : deviceInfos) {
                    HospitalDevice device = deviceInfo.getDevice();

                    if (device.getLastUpdate().getTime() >= lastUpdate && device.getLastUpdate().getTime() <= now) {
                        jsonDevices.put(new JSONObject(gson.toJson(device)));
                    }

                    List<ReportInfo> reports = deviceInfo.getReports();

                    for(ReportInfo reportInfo : reports) {
                        Report report = reportInfo.getReport();

                        if(report.getCreated().getTime() >= lastUpdate && report.getCreated().getTime() <= now) {
                            jsonReports.put(new JSONObject(gson.toJson(report)));
                        }
                    }
                }
            }

            JSONObject data = new JSONObject();

            JSONObject request = new JSONObject(params);

            data.put(ResourceKeys.DEVICES, jsonDevices);
            data.put(ResourceKeys.USERS, jsonUsers);
            data.put(ResourceKeys.REPORTS, jsonReports);

            request.put(ResourceKeys.DATA, data);

            return new HospitalRequest(context, url, request, executor);
        } catch(JSONException e) {
            return null;
        }
    }

    private class HospitalRequest extends JsonObjectRequest {

        private HospitalRequest(final Context context, final String url, JSONObject request, ExecutorService executor) {
            super(Request.Method.POST, url, request, response -> executor.execute(() -> {
                try {
                    Log.i(HospitalRepository.this.getClass().getName(), "Server Response:\n" + response.toString(4));

                    HospitalInfo hospitalInfo = ResponseParser.parseHospital(response);

                    long now = new Date().getTime();

                    if (hospitalInfo.getLastUpdate().getTime() > now) {
                        hospitalInfo.setLastUpdate(new Date(now));
                    }

                    Hospital hospital = new Hospital(hospitalInfo.getId(), hospitalInfo.getName(), hospitalInfo.getLocation(), hospitalInfo.getLongitude(), hospitalInfo.getLatitude(), hospitalInfo.getLastUpdate());

                    HospitalRepository.this.updateHospitalSync(hospital);

                    for (User user : hospitalInfo.getUsers()) {
                        if (user.getLastUpdate().getTime() > now) {
                            user.setLastUpdate(new Date(now));
                        }

                        HospitalRepository.this.updateUserSync(user);
                    }

                    for (DeviceInfo deviceInfo : hospitalInfo.getDevices()) {
                        if (deviceInfo.getDevice().getLastUpdate().getTime() > now) {
                            deviceInfo.getDevice().setLastUpdate(new Date(now));
                        }

                        HospitalRepository.this.updateDeviceSync(deviceInfo.getDevice());

                        List<ReportInfo> reportInfos = deviceInfo.getReports();

                        for (ReportInfo reportInfo : reportInfos) {
                            Report report = reportInfo.getReport();

                            HospitalRepository.this.updateReportSync(report);
                        }
                    }

                    SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(Defaults.LAST_SYNC_PREFERENCE, new Date().getTime());
                    editor.apply();
                } catch(Exception e) {
                    Log.e(HospitalRepository.this.getClass().getName(), e.toString(), e);
                    //we cannot show any information to the user from here as it runs in an extra thread
                }
            }), error -> {
                //we cannot show any information to the user from here as it runs in an extra thread
            });
        }
    }

    private boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }
}
