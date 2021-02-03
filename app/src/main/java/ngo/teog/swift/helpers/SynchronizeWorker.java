package ngo.teog.swift.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

import dagger.android.HasAndroidInjector;
import ngo.teog.swift.communication.DataAction;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.ResponseParser;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UtcDateTypeAdapter;

/**
 * This worker performs a synchronization with the server in the background.
 * @author nitelow
 */
public class SynchronizeWorker extends Worker {

    /** Tag that identifies periodic background sync task. Must be set explicitly when queueing worker. */
    public static final String TAG = "periodic_background_synchronization";

    @Inject
    HospitalRepository hospitalRepository;

    public SynchronizeWorker(Context context, WorkerParameters params) {
        super(context, params);

        ContextInjection.inject(this, context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(this.getClass().getName(), "synchronize worker running");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        long lastSync = preferences.getLong(Defaults.LAST_SYNC_PREFERENCE, 0);
        long now = new Date().getTime();

        //check whether last sync was at least 30 mins ago
        if(now >= (lastSync + 30*60*1000)) {
            int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            ExecutorService executorService = Executors.newSingleThreadExecutor();//TODO maybe use future instead of spawning another thread? -> would make errors more transparent
            RequestQueue queue = VolleyManager.getInstance(getApplicationContext()).getRequestQueue();

            HospitalRequest hospitalRequest = createHospitalRequest(getApplicationContext(), userId, executorService);

            if(hospitalRequest != null) {
                queue.add(hospitalRequest);
            }
        }

        return Result.success();
    }

    static class ContextInjection {
        static void inject(Object target, Context context) {
            ((HasAndroidInjector)context.getApplicationContext()).androidInjector().inject(target);
        }
    }

    private HospitalRequest createHospitalRequest(Context context, int userID, ExecutorService executorService) {
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

            //the server then must resolve possible collisions (e.g. when multiple users have created a report with the same ID)
            Map<String, String> params = RequestFactory.generateParameterMap(context, DataAction.PERIODIC_SYNC_HOSPITAL_INFO, true);
            params.put(ResourceKeys.LAST_SYNC, dateFormat.format(new Date(lastUpdate)));

            JSONArray jsonDevices = new JSONArray();
            JSONArray jsonUsers = new JSONArray();
            JSONArray jsonReports = new JSONArray();

            //assure that no dataset with invalid timestamp is synchronized to server
            long now = new Date().getTime();

            List<User> users = hospitalRepository.getUserColleaguesSync(userID);

            if(users != null) {
                for (User user : users) {
                    if(user.getLastUpdate().getTime() >= lastUpdate && user.getLastUpdate().getTime() <= now) {
                        jsonUsers.put(new JSONObject(gson.toJson(user)));
                    }
                }
            }

            List<DeviceInfo> deviceInfos = hospitalRepository.getHospitalDevicesSync(userID);

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

            return new HospitalRequest(context, url, request, executorService);
        } catch(JSONException e) {
            return null;
        }
    }

    private class HospitalRequest extends JsonObjectRequest {

        private HospitalRequest(final Context context, final String url, JSONObject request, ExecutorService executorService) {
            super(Request.Method.POST, url, request, response -> executorService.execute(() -> {
                try {
                    Log.i(SynchronizeWorker.this.getClass().getName(), "Server Response:\n" + response.toString(4));

                    HospitalInfo hospitalInfo = ResponseParser.parseHospital(response);

                    long now = new Date().getTime();

                    if (hospitalInfo.getLastUpdate().getTime() > now) {
                        hospitalInfo.setLastUpdate(new Date(now));
                    }

                    Hospital hospital = new Hospital(hospitalInfo.getId(), hospitalInfo.getName(), hospitalInfo.getLocation(), hospitalInfo.getLongitude(), hospitalInfo.getLatitude(), hospitalInfo.getLastUpdate());

                    hospitalRepository.updateHospitalSync(hospital);

                    for (User user : hospitalInfo.getUsers()) {
                        if (user.getLastUpdate().getTime() > now) {
                            user.setLastUpdate(new Date(now));
                        }

                        hospitalRepository.updateUserSync(user);
                    }

                    for (DeviceInfo deviceInfo : hospitalInfo.getDevices()) {
                        if (deviceInfo.getDevice().getLastUpdate().getTime() > now) {
                            deviceInfo.getDevice().setLastUpdate(new Date(now));
                        }

                        hospitalRepository.updateDeviceSync(deviceInfo.getDevice());

                        List<ReportInfo> reportInfos = deviceInfo.getReports();

                        for (ReportInfo reportInfo : reportInfos) {
                            Report report = reportInfo.getReport();

                            hospitalRepository.updateReportSync(report);
                        }
                    }

                    SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(Defaults.LAST_SYNC_PREFERENCE, new Date().getTime());
                    editor.apply();

                    executorService.shutdown();
                } catch(Exception e) {
                    Log.e(SynchronizeWorker.this.getClass().getName(), e.toString(), e);
                    //we cannot show any information to the user from here as it runs in an extra thread
                }
            }), error -> {
                //we cannot show any information to the user from here as it runs in an extra thread
            });
        }
    }
}
