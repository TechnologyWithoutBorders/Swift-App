package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;

import java.util.Date;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.Report;

public class ReportCreationActivity extends BaseActivity {

    private EditText descriptionText;
    private ProgressBar progressBar;
    private Button saveButton;

    private int oldState;
    private int state;
    private int device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        Intent intent = getIntent();
        oldState = intent.getIntExtra("OLD_STATUS", -1);
        state = intent.getIntExtra("NEW_STATUS", -1);
        device = intent.getIntExtra("DEVICE", -1);

        descriptionText = findViewById(R.id.descriptionText);
        progressBar = findViewById(R.id.progressBar);
        saveButton = findViewById(R.id.saveButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.reportcreation_activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createReport(View view) {
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        String description = descriptionText.getText().toString();

        Report report = new Report(-1, preferences.getInt(Defaults.ID_PREFERENCE, -1), null, device, oldState, state, description, new Date());

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        RequestFactory factory = new RequestFactory();
        RequestFactory.DefaultRequest request = factory.createReportCreationRequest(this, progressBar, saveButton, report);

        saveButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        queue.add(request);
    }
}
