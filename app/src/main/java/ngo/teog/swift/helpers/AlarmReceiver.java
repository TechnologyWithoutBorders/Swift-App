package ngo.teog.swift.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        RequestFactory.NewsListRequest request = new RequestFactory().createNewsRequest(context);
        VolleyManager.getInstance(context).getRequestQueue().add(request);
    }
}