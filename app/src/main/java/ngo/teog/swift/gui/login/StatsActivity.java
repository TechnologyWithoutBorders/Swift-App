package ngo.teog.swift.gui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ngo.teog.swift.R;

/**
 * Activity that will be used to display some key statistics, mainly for demonstration purposes.
 * It can be opened from the login screen and is therefore accessible to anyone.
 * @author Julian Deyerler
 */
public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
    }
}
