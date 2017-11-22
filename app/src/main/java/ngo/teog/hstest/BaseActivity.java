package ngo.teog.hstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by Julian on 17.11.2017.
 */

public class BaseActivity extends AppCompatActivity {

    private BroadcastReceiver internetReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        internetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO nur bei Änderungen
                onInternetStatusChanged();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_ACTION);

        this.registerReceiver(internetReceiver, filter);
    }

    public void onInternetStatusChanged() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(internetReceiver);
    }
}
