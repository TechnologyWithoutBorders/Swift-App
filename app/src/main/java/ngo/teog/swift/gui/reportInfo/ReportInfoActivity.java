package ngo.teog.swift.gui.reportInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Activity that sums up all available information about a report.
 */
public class ReportInfoActivity extends BaseActivity {

    private DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN);

    @Inject
    ViewModelFactory viewModelFactory;

    private ReportInfo reportInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        int reportId = intent.getIntExtra(ResourceKeys.REPORT_ID, -1);

        TextView dateView = findViewById(R.id.dateView);
        TextView authorView = findViewById(R.id.authorView);
        ImageView fromState = findViewById(R.id.fromState);
        ImageView toState = findViewById(R.id.toState);
        TextView descriptionView = findViewById(R.id.descriptionView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        ReportInfoViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReportInfoViewModel.class);
        viewModel.init(userId, deviceId, reportId);

        viewModel.getReportInfo().observe(this, reportInfo -> {
            if(reportInfo != null) {
                this.reportInfo = reportInfo;

                Report report = reportInfo.getReport();
                User author = reportInfo.getAuthors().get(0);

                DeviceStateVisuals previousStateInfo = new DeviceStateVisuals(report.getPreviousState(),this);
                fromState.setImageDrawable(previousStateInfo.getStateIcon());
                fromState.setBackgroundColor(previousStateInfo.getBackgroundColor());

                DeviceStateVisuals currentStateInfo = new DeviceStateVisuals(report.getCurrentState(),this);
                toState.setImageDrawable(currentStateInfo.getStateIcon());
                toState.setBackgroundColor(currentStateInfo.getBackgroundColor());

                dateView.setText(dateFormat.format(report.getCreated()));
                authorView.setText(author.getName());
                descriptionView.setText(report.getDescription());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item, R.string.reportInfoActivity);
        }
    }
}


