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
 * Globaler Receiver, der getriggert wird, wenn das Android-Gerät gebootet hat.
 * Der Receiver erstmal nur eine Testbenachrichtigung. Da die wiederkehrende Prüfung auf abgelaufene
 * Deadlines mithilfe der JobScheduler-API laufen wird, bin ich mir noch nocht sicher, ob dieser
 * Receiver bestehen bleibt.
 *
 * Created by Julian on 12.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Test Benachrichtigung
            int mNotificationId = 0;
            String CHANNEL_ID = "dummy_channel";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.logo72x72)
                            .setContentTitle("Notification Test")
                            .setContentText("Swift App hast detected a device reboot.");
            Intent resultIntent = new Intent(context, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(mNotificationId, mBuilder.build());
        }
    }
}
