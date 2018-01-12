package ngo.teog.hstest.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import ngo.teog.hstest.MainActivity;
import ngo.teog.hstest.R;

/**
 * Wird getriggert, wenn die Geräte-Deadlines geprüft werden sollen.
 * Created by Julian on 21.12.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*//Test Benachrichtigung
        int mNotificationId = id;
        String CHANNEL_ID = "dummy_channel";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Notification Test")
                .setContentText("Swift App has just checked for device that need maintenance.");
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mNotificationId, mBuilder.build());

        id++;*/
    }
}