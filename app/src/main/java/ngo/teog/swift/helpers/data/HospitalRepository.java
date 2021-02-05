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

    public LiveData<HospitalDump> getHospitalDump(int userId) {
        return hospitalDao.loadHospitalDump(userId);
    }

    public LiveData<User> getUser(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUser(userId);
    }

    public LiveData<Hospital> getUserHospital(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserHospital(userId);
    }

    public LiveData<UserInfo> getUserProfileInfo(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserInfo(userId);
    }

    public LiveData<List<User>> getUserColleagues(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadUserColleagues(userId);
    }

    public List<User> getUserColleaguesSync(int userId) {
        return hospitalDao.getUserColleagues(userId);
    }

    public List<DeviceInfo> getHospitalDevicesSync(int userId) {
        return hospitalDao.getHospitalDevices(userId);
    }

    public LiveData<DeviceInfo> getDevice(int userId, int deviceId) {
        refreshUserHospital(userId);

        return hospitalDao.loadDevice(deviceId);
    }

    public LiveData<List<DeviceInfo>> getHospitalDevices(int userId) {
        refreshUserHospital(userId);

        return hospitalDao.loadHospitalDevices(userId);
    }

    public LiveData<ReportInfo> loadReportInfo(int userId, int deviceId, int reportId) {
        refreshUserHospital(userId);

        return hospitalDao.loadReportInfo(deviceId, reportId);
    }

    public LiveData<UserInfo> getUserInfo(int myId, int userId) {
        refreshUserHospital(myId);

        return hospitalDao.loadUserInfo(userId);
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

    private void refreshUserHospitalSync(int userId) {
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
            //TODO muss Transaction sein, aber Vorsicht: Inserts sind synchron, Queries nicht!

            int maxReportId = hospitalDao.getMaxReportId(report.getDevice());
            report.setId(maxReportId+1);

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

            List<User> users = HospitalRepository.this.getUserColleaguesSync(userID);

            if(users != null) {
                for (User user : users) {
                    if(user.getLastUpdate().getTime() >= lastUpdate && user.getLastUpdate().getTime() <= now) {
                        jsonUsers.put(new JSONObject(gson.toJson(user)));
                    }
                }
            }

            List<DeviceInfo> deviceInfos = HospitalRepository.this.getHospitalDevicesSync(userID);

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
