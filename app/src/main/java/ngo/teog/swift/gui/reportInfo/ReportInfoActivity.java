package ngo.teog.swift.gui.reportInfo;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class ReportInfoActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private ReportInfo reportInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int reportId = intent.getIntExtra(Defaults.REPORT_ID_KEY, -1);

        TextView dateView = findViewById(R.id.dateView);
        TextView authorView = findViewById(R.id.authorView);
        TextView stateChangeView = findViewById(R.id.stateChangeView);
        TextView descriptionView = findViewById(R.id.descriptionView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        ReportInfoViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReportInfoViewModel.class);
        viewModel.init(userId, reportId);

        viewModel.getReportInfo().observe(this, reportInfo -> {
            if(reportInfo != null) {
                this.reportInfo = reportInfo;

                Report report = reportInfo.getReport();
                User author = reportInfo.getAuthors().get(0);

                dateView.setText(Defaults.DATETIME_FORMAT.format(report.getCreated()));
                authorView.setText(author.getName());
                stateChangeView.setText(getResources().getStringArray(R.array.device_states)[report.getPreviousState()] + " -> " + getResources().getStringArray(R.array.device_states)[report.getCurrentState()]);
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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.reportInfoActivity);
                return true;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this report: http://teog.virlep.de/report/" + Integer.toString(reportInfo.getReport().getId()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share report link"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


