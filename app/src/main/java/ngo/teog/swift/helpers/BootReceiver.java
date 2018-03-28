package ngo.teog.swift.helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

/**
 * Globaler Receiver, der getriggert wird, wenn das Android-Gerät gebootet hat.
 * Erstellt einen AlarmManager (JobScheduler ist erst ab API-Level 21 möglich),
 * der immer im Hintergrund läuft (auch wenn die App geschlossen ist) und zyklisch einen Alarm
 * auslöst, wenn die Geräte-Deadlines geprüft werden sollen.
 *
 * Created by Julian on 12.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(Build.VERSION.SDK_INT >= 26) {
                NotificationManager mNotificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                // The id of the channel.
                String id = "news_channel";
                // The user-visible name of the channel.
                CharSequence name = "News";
                // The user-visible description of the channel.
                String description = "Description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            if(pendingIntent == null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
            }
        }
    }
}
