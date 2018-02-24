package ngo.teog.swift.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import ngo.teog.swift.MainActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.comm.RequestFactory;
import ngo.teog.swift.comm.VolleyManager;

/**
 * Wird getriggert, wenn die Geräte-Deadlines geprüft werden sollen.
 * Created by Julian on 21.12.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(checkForInternetConnection(context)) {
            SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            if(preferences.contains(context.getString(R.string.id_pref)) && preferences.contains(context.getString(R.string.pw_pref))) {

                RequestFactory.NewsListRequest request = new RequestFactory().createNewsRequest(context);
                VolleyManager.getInstance(context).getRequestQueue().add(request);
            }
        }
    }

    private boolean checkForInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }
}