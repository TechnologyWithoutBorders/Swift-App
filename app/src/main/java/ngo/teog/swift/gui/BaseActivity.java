package ngo.teog.swift.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import ngo.teog.swift.R;

public abstract class BaseActivity extends AppCompatActivity {
    public void showInfo(int stringID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(stringID))
                .setTitle("Information")
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ignore
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
