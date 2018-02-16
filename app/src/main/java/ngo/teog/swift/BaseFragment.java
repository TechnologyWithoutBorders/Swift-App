package ngo.teog.swift;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Überklasse für alle Activities. Hier können Funktionen implementiert werden,
 * die wir immer wieder verwenden. Z.B. wird in jeder Activity ein BroadcastReceiver implementiert,
 * der bei einer Änderung des Internetstatus die Methode onInternetStatusChanged() aufruft.
 * Eine Activity, die diese Funktion nutzt, muss also lediglich diese Methode überschreiben.
 * Created by Julian on 17.11.2017.
 */

public class BaseFragment extends Fragment {
    protected boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
