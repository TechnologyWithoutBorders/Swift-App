package ngo.teog.swift.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import ngo.teog.swift.R;

public abstract class BaseActivity extends AppCompatActivity {
    public void showInfo(int stringID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(Html.fromHtml(getString(stringID)))
                .setTitle(getString(R.string.info_dialog_heading))
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setPositiveButton(getString(R.string.info_dialog_confirmation), new DialogInterface.OnClickListener() {
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

            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
