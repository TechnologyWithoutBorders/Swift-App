package ngo.teog.swift.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;

public class UpdateWorker extends Worker {

    public static final String TAG = "update_todo";

    private Context context;

    public UpdateWorker(Context context, WorkerParameters params) {
        super(context, params);

        this.context = context;
    }

    @Override
    public Worker.Result doWork() {
        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        if(preferences.contains(Defaults.ID_PREFERENCE) && preferences.contains(Defaults.PW_PREFERENCE)) {

            RequestFactory.DefaultRequest request = RequestFactory.getInstance().createWorkRequest(context, preferences.getInt(Defaults.ID_PREFERENCE, -1), preferences.getInt(Defaults.NOTIFICATION_COUNTER, 0));
            VolleyManager.getInstance(context).getRequestQueue().add(request);

            return Result.success();
        } else {
            return Result.failure();
        }
    }
}
