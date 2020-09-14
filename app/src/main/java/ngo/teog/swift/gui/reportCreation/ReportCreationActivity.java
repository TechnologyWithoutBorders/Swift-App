package ngo.teog.swift.gui.reportCreation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import java.util.Date;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.ResourceKeys;
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
    private int device;
    private int hospital;

    private Spinner stateSpinner;

    @Inject
    ViewModelFactory viewModelFactory;

    private ReportCreationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);

        Intent intent = getIntent();
        oldState = intent.getIntExtra(ResourceKeys.REPORT_OLD_STATE, -1);
        device = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        hospital = intent.getIntExtra(ResourceKeys.HOSPITAL_ID, -1);

        stateSpinner = findViewById(R.id.stateSpinner);
        stateSpinner.setAdapter(new StatusArrayAdapter(this, getResources().getStringArray(R.array.device_states)));
        stateSpinner.setSelection(oldState);

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
        inflater.inflate(R.menu.menu_report_creation, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.reportcreation_activity);
    }

    public void createReport(View view) {
        int newState = stateSpinner.getSelectedItemPosition();

        if(newState != oldState) {
            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            String description = descriptionText.getText().toString().trim();

            Report report = new Report(0, preferences.getInt(Defaults.ID_PREFERENCE, -1), device, hospital, oldState, newState, description, new Date());

            viewModel.createReport(report, preferences.getInt(Defaults.ID_PREFERENCE, -1));

            saveButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            ReportCreationActivity.this.finish();
        } else {
            Toast.makeText(this.getApplicationContext(), "new state can not be the same as old state", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter for displaying the device state spinner.
     */
    private class StatusArrayAdapter extends ArrayAdapter<String> {

        private final Context context;

        private StatusArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_status, parent, false);
            }

            TextView statusTextView = convertView.findViewById(R.id.statusTextView);
            statusTextView.setText(getItem(position));

            ImageView statusImageView = convertView.findViewById(R.id.statusImageView);

            DeviceStateVisuals triple = new DeviceStateVisuals(position,this.getContext());

            statusImageView.setImageDrawable(triple.getStateIcon());
            statusImageView.setBackgroundColor(triple.getBackgroundColor());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }
}
