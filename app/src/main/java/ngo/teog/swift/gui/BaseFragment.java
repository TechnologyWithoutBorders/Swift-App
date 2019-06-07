package ngo.teog.swift.gui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.fragment.app.Fragment;

/**
 * Überklasse für alle Activities. Hier können Funktionen implementiert werden,
 * die wir immer wieder verwenden. Z.B. wird in jeder Activity ein BroadcastReceiver implementiert,
 * der bei einer Änderung des Internetstatus die Methode onInternetStatusChanged() aufruft.
 * Eine Activity, die diese Funktion nutzt, muss also lediglich diese Methode überschreiben.
 * Created by Julian on 17.11.2017.
 */

public abstract class BaseFragment extends Fragment {
    protected boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }
}
