package ngo.teog.swift.gui;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import ngo.teog.swift.R;

public abstract class BaseActivity extends AppCompatActivity {
    public void showInfo(int stringID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(stringID))
                .setTitle("Information")
                .setIcon(R.drawable.ic_info_outline_black_24dp);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
