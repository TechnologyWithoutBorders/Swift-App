package ngo.teog.swift.gui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ngo.teog.swift.R;

/**
 * Parent class for all activities.<br>
 * Implements some basic functionality (like the "info" menu) that can/should be used across all activities.
 * @author nitelow
 */
public abstract class BaseActivity extends AppCompatActivity {
    public void showInfo(int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(Html.fromHtml(getString(stringId)))
                .setTitle(getString(R.string.info_dialog_heading))
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setPositiveButton(getString(R.string.info_dialog_confirmation), (dialog, id) -> {
                    //ignore
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean onOptionsItemSelected(MenuItem item, int stringId) {
        if(item.getItemId() == R.id.info) {
            showInfo(stringId);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Checks whether the device is connected to a network (WiFi or mobile).
     * @return true if device is connected, otherwise false
     */
    public boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }
}
