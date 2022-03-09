package ngo.teog.swift.gui.reportInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.reportCreation.ReportCreationActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Shows all available information about a report.
 * @author nitelow
 */
public class ReportInfoActivity extends BaseActivity {

    private boolean resumed = false;

    @Inject
    ViewModelFactory viewModelFactory;

    private int userId;

    private RecyclerView reportThreadView;
    private Button reportCreationButton;

    private ReportInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        TextView titleView = findViewById(R.id.title_view);
        reportThreadView = findViewById(R.id.report_thread_view);
        reportCreationButton = findViewById(R.id.reportCreationButton);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(ReportInfoViewModel.class);
        viewModel.init(userId, deviceId).observe(this, observable -> viewModel.refreshDevice());

        viewModel.getDeviceInfo().observe(this, deviceInfo -> {
            if(deviceInfo != null) {
                reportCreationButton.setOnClickListener((view) -> {
                    Intent reportIntent = new Intent(ReportInfoActivity.this, ReportCreationActivity.class);
                    reportIntent.putExtra(ResourceKeys.HOSPITAL_ID, deviceInfo.getHospital().getId());
                    reportIntent.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                    startActivity(reportIntent);
                });

                titleView.setText(deviceInfo.getDevice().getType());

                List<ReportInfo> reports = deviceInfo.getReports();
                Collections.sort(reports, (first, second) -> first.getReport().getId()-second.getReport().getId());

                ReportThreadAdapter adapter = new ReportThreadAdapter(this, reports);
                reportThreadView.setAdapter(adapter);
                reportThreadView.setLayoutManager(new LinearLayoutManager(this));

                if(adapter.getItemCount() > 0) {
                    reportThreadView.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(resumed) {
            Log.i(this.getClass().getName(), "activity has resumed, refreshing...");
            refresh();
        } else {
            resumed = true;
        }
    }

    private void refresh() {
        viewModel.refreshDevice();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_report_info, menu);
        return true;
    }

    //TODO
    /*private void shareReport(ReportInfo reportInfo) {
        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Intent intent = new Intent(Intent.ACTION_SEND);

        String assetString = getString(R.string.report).toLowerCase();
        String sharingString = String.format(getString(R.string.want_to_show), assetString, Defaults.HOST, assetString, preferences.getString(Defaults.COUNTRY_PREFERENCE, null), reportInfo.getHospital().getId());
        intent.putExtra(Intent.EXTRA_TEXT, sharingString + reportInfo.getReport().getDevice() + "/" + reportInfo.getReport().getId());
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_link)));
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            //Build tutorial
            FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                    .add(buildTutorialStep(reportThreadView, getString(R.string.device_info_tutorial_report_list), Gravity.TOP))
                    .add(buildTutorialStep(reportCreationButton, getString(R.string.device_info_tutorial_report_creation), Gravity.CENTER));

            tutorialQueue.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReportThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int OWN_REPORT = 0;
        private static final int OTHER_REPORT = 1;

        private final Context context;
        private final List<ReportInfo> reportList;

        public ReportThreadAdapter(Context context, List<ReportInfo> reportList) {
            this.context = context;
            this.reportList = reportList;
        }

        @Override
        public int getItemCount() {
            return reportList.size();
        }

        @Override
        public int getItemViewType(int position) {
            Report report = reportList.get(position).getReport();

            if(report.getAuthor() == userId) {
                return OWN_REPORT;
            } else {
                return OTHER_REPORT;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == OWN_REPORT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_thread_own, parent, false);
                return new OwnReportHolder(this.context, view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_thread_other, parent, false);
                return new OtherReportHolder(this.context, view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ReportInfo reportInfo = reportList.get(position);

            switch(holder.getItemViewType()) {
                case OWN_REPORT:
                    ((OwnReportHolder) holder).bind(reportInfo);
                    break;
                case OTHER_REPORT:
                    ((OtherReportHolder) holder).bind(reportInfo);
            }
        }

        private class OtherReportHolder extends RecyclerView.ViewHolder {
            private final ImageView toState;
            private final TextView dateView, authorView, titleView, descriptionView;

            private final Context context;

            public OtherReportHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;

                toState = itemView.findViewById(R.id.toState);
                dateView = itemView.findViewById(R.id.date_view);
                authorView = itemView.findViewById(R.id.author_view);
                titleView = itemView.findViewById(R.id.title_view);
                descriptionView = itemView.findViewById(R.id.description_view);
            }

            public void bind(ReportInfo reportInfo) {
                Report report = reportInfo.getReport();

                DeviceStateVisuals stateVisuals = new DeviceStateVisuals(report.getCurrentState(),this.context);

                toState.setImageDrawable(stateVisuals.getStateIcon());
                toState.setColorFilter(stateVisuals.getBackgroundColor());

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN, Locale.getDefault());
                dateView.setText(dateFormat.format(report.getCreated()));

                authorView.setText(getString(R.string.report_author_name, reportInfo.getAuthor().getName()));
                titleView.setText(report.getTitle());
                descriptionView.setText(report.getDescription());
            }
        }

        private class OwnReportHolder extends RecyclerView.ViewHolder {
            private final ImageView toState;
            private final TextView dateView, titleView, descriptionView;

            private final Context context;

            public OwnReportHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;

                toState = itemView.findViewById(R.id.toState);
                dateView = itemView.findViewById(R.id.date_view);
                titleView = itemView.findViewById(R.id.title_view);
                descriptionView = itemView.findViewById(R.id.description_view);
            }

            public void bind(ReportInfo reportInfo) {
                Report report = reportInfo.getReport();

                DeviceStateVisuals stateVisuals = new DeviceStateVisuals(report.getCurrentState(),this.context);

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN, Locale.getDefault());
                dateView.setText(dateFormat.format(report.getCreated()));

                toState.setImageDrawable(stateVisuals.getStateIcon());
                toState.setColorFilter(stateVisuals.getBackgroundColor());

                titleView.setText(report.getTitle());
                descriptionView.setText(report.getDescription());
            }
        }
    }
}


