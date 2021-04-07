package ngo.teog.swift.gui.reportInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
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
 * Shows all available information about a report.
 * @author nitelow
 */
public class ReportInfoActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private ReportInfo reportInfo;

    private LinearLayout stateChange;
    private LinearLayout descriptionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        int reportId = intent.getIntExtra(ResourceKeys.REPORT_ID, -1);

        TextView dateView = findViewById(R.id.dateView);
        TextView authorView = findViewById(R.id.authorView);
        TextView titleView = findViewById(R.id.title_view);
        TextView fromStateText = findViewById(R.id.fromStateText);
        TextView toStateText = findViewById(R.id.toStateText);
        ImageView fromState = findViewById(R.id.fromState);
        ImageView toState = findViewById(R.id.toState);
        TextView descriptionView = findViewById(R.id.descriptionView);

        stateChange = findViewById(R.id.state_change);
        descriptionLayout = findViewById(R.id.descriptionLayout);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        ReportInfoViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(ReportInfoViewModel.class);
        viewModel.init(userId, deviceId, reportId);

        viewModel.getReportInfo().observe(this, reportInfo -> {
            this.reportInfo = reportInfo;

            if(reportInfo != null) {
                Report report = reportInfo.getReport();
                User author = reportInfo.getAuthor();

                DeviceStateVisuals previousStateInfo = new DeviceStateVisuals(report.getPreviousState(), this);
                fromState.setImageDrawable(previousStateInfo.getStateIcon());
                fromState.setBackgroundColor(previousStateInfo.getBackgroundColor());
                fromStateText.setText(previousStateInfo.getStateString());

                DeviceStateVisuals currentStateInfo = new DeviceStateVisuals(report.getCurrentState(), this);
                toState.setImageDrawable(currentStateInfo.getStateIcon());
                toState.setBackgroundColor(currentStateInfo.getBackgroundColor());
                toStateText.setText(currentStateInfo.getStateString());

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN, Locale.getDefault());
                dateView.setText(dateFormat.format(report.getCreated()));
                authorView.setText(author.getName());

                String title = report.getTitle();
                if (title.length() > 0) {
                    titleView.setText(title);
                }

                if (!report.getDescription().isEmpty()){
                    descriptionView.setText(report.getDescription());
                }

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
        if(item.getItemId() == R.id.share) {
            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            Intent intent = new Intent(Intent.ACTION_SEND);

            String assetString = getString(R.string.report).toLowerCase();
            String sharingString = String.format(getString(R.string.want_to_show), assetString, Defaults.HOST, assetString, preferences.getString(Defaults.COUNTRY_PREFERENCE, null), reportInfo.getHospital().getId());
            intent.putExtra(Intent.EXTRA_TEXT, sharingString + reportInfo.getReport().getDevice() + "/" + reportInfo.getReport().getId());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share_link)));

            return true;
        } else if (item.getItemId() == R.id.info) {
            //Build tutorial
            FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                    .add(buildTutorialStep(stateChange, getString(R.string.report_tutorial_state_change), Gravity.CENTER))
                    .add(buildTutorialStep(descriptionLayout, getString(R.string.report_tutorial_description), Gravity.TOP));

            tutorialQueue.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


