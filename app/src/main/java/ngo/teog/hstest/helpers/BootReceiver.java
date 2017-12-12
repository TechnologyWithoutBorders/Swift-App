package ngo.teog.hstest.helpers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Globaler Receiver, der getriggert wird, wenn das Android-Gerät gebootet hat.
 * Der Receiver erstellt einen AlarmManager, der alle paar Minuten prüft, ob eine Wartung/Reparatur
 * ansteht und dementsprechend Push-Benachrichtigungen erstellt.
 *
 * Es muss auch beim Start der App immer geprüft wird, ob dieser AlarmManager läuft, weil
 * ein Gerät unter Umständen lange Zeit nicht neu gestartet wird. So lange wird auch dieser
 * Receiver nicht getriggert.
 * Created by Julian on 12.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                //TODO Geräte prüfen und Benachrichtigungen erstellen.
            }
        }
    }
}
