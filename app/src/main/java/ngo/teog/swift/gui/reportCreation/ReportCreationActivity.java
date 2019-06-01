package ngo.teog.swift.gui.reportCreation;

import android.arch.lifecycle.ViewModelProviders;
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

import java.util.Date;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class ReportCreationActivity extends BaseActivity {

    private EditText descriptionText;
    private ProgressBar progressBar;
    private Button saveButton;

    private int oldState;
    private int state;
    private int device;

    public static final String OLD_STATE_KEY = "OLD_STATE";
    public static final String NEW_STATE_KEY = "NEW_STATE";

    @Inject
    ViewModelFactory viewModelFactory;

    private ReportCreationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        Intent intent = getIntent();
        oldState = intent.getIntExtra(OLD_STATE_KEY, -1);
        state = intent.getIntExtra(NEW_STATE_KEY, -1);
        device = intent.getIntExtra(Defaults.DEVICE_ID_KEY, -1);

        descriptionText = findViewById(R.id.descriptionText);
        progressBar = findViewById(R.id.progressBar);
        saveButton = findViewById(R.id.saveButton);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReportCreationViewModel.class);
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

        Report report = new Report(0, preferences.getInt(Defaults.ID_PREFERENCE, -1), device, oldState, state, description, new Date());

        viewModel.createReport(report, preferences.getInt(Defaults.ID_PREFERENCE, -1));

        saveButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        ReportCreationActivity.this.finish();
    }
}
