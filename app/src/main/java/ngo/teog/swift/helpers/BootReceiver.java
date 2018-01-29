package ngo.teog.swift.helpers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Globaler Receiver, der getriggert wird, wenn das Android-Gerät gebootet hat.
 * Erstellt einen AlarmManager (JobScheduler ist erst ab API-Level 21 möglich),
 * der immer im Hintergrund läuft (auch wenn die App geschlossen ist) und zyklisch einen Alarm
 * auslöst, wenn die Geräte-Deadlines geprüft werden sollen.
 *
 * Created by Julian on 12.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60 * 1000, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
            }
        }
    }
}
