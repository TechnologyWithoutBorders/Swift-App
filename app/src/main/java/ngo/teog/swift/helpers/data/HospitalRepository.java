package ngo.teog.swift.helpers.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResponseParser;
import ngo.teog.swift.helpers.filters.UserFilter;

@Singleton
public class HospitalRepository {

    private final HospitalDao hospitalDao;
    private final Context context;
    private ExecutorService executor;

    @Inject
    public HospitalRepository(HospitalDao hospitalDao, Context context) {
        this.hospitalDao = hospitalDao;
        this.context = context;
        //TODO use previously defined executor
        this.executor = executor;
    }

    public LiveData<Hospital> getHospital(int id) {
        refreshHospital(id);
        return hospitalDao.load(id);
    }

    private void refreshHospital(final int id) {
        //runs in a background thread.
        executor = Executors.newCachedThreadPool();

        executor.execute(() -> {
            //check if user data has been fetched recently
            boolean hospitalExists = (hospitalDao.hasHospital(id, System.currentTimeMillis(), 10) != 0);

            if(!hospitalExists) {
                //refresh the data.

                /*Constraints updateConstraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                Data inputData = new Data.Builder()
                        .putInt("id", id)
                        .build();

                //TODO hier funktioniert die Dependency Injection nicht, der Workaround ist aber ziemlich umständliche
                OneTimeWorkRequest updateWork = new OneTimeWorkRequest.Builder(UpdateWorker.class)
                        .addTag("update_profile")
                        .setConstraints(updateConstraints)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance().enqueue(updateWork);*/

                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

                HospitalRequest hospitalRequest = createHospitalRequest(context, id, executor);

                queue.add(hospitalRequest);
            }
        });
    }

    private HospitalRequest createHospitalRequest(Context context, int id, ExecutorService executor) {
        final String url = Defaults.BASE_URL + Defaults.HOSPITALS_URL;

        //TODO man könnte auch immer das last_update mitschicken und nur aktualisieren, wenn es neuere Daten gibt -> Netzwerkaufwand minimieren
        //TODO aber eigentlich übertragen wir ja auch jetzt schon ziemlich wenige Daten
        Map<String, String> params = generateParameterMap(context, "fetch_hospital", true);
        params.put("h_ID", Integer.toString(id));

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
                            Hospital hospital = new ResponseParser().parseHospitalList(response).get(0);

                            // Updates the database. The LiveData object automatically
                            // refreshes, so we don't need to do anything else here.
                            hospitalDao.save(hospital);
                        } catch(Exception e) {
                            Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    executor.shutdown();
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
}
