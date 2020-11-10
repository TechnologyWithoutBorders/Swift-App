package ngo.teog.swift.gui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import ngo.teog.swift.R;

/**
 * Parent class for all activities.<br>
 * Implements some basic functionality (like the "info" menu) that can/should be used across all activities.
 * @author nitelow
 */
public abstract class BaseActivity extends AppCompatActivity {
    public void showInfo(int... stringIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        StringBuilder info = new StringBuilder();

        for(int id : stringIds) {
            info.append(Html.fromHtml(getString(id)));
        }

        builder.setMessage(info)
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

    /**
     * Creates a displayable tutorial step that can be used in a tutorial queue.
     * @param viewToFocus view that should be focused during step
     * @param message message to display during step
     * @return displayable tutorial step
     */
    public FancyShowCaseView buildTutorialStep(View viewToFocus, String message) {
        return new FancyShowCaseView.Builder(this)
                .focusOn(viewToFocus)
                .title(message)
                .titleSize(25, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(60)
                .build();
    }
}
