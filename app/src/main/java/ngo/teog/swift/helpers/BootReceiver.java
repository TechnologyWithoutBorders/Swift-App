package ngo.teog.swift.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Julian on 12.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
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

        Constraints updateConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest updateWork = new PeriodicWorkRequest.Builder(UpdateWorker.class, 1, TimeUnit.HOURS)
                .addTag(UpdateWorker.TAG)
                .setConstraints(updateConstraints)
                .build();

        WorkManager.getInstance(context).enqueue(updateWork);
    }
}
