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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
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

    private int userId, deviceId, hospitalId;

    private RecyclerView reportThreadView;
    private CardView reportForm;
    private EditText titleText, descriptionText;
    private Spinner stateSpinner;

    private ReportInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        hospitalId = intent.getIntExtra(ResourceKeys.HOSPITAL_ID, -1);
        //TODO: scroll to report

        reportThreadView = findViewById(R.id.report_thread_view);

        reportForm = findViewById(R.id.reportForm);
        titleText = findViewById(R.id.report_title);
        descriptionText = findViewById(R.id.descriptionText);

        List<Integer> states = new ArrayList<>(DeviceState.IDS.length+1);
        states.add(-1);

        for(int s : DeviceState.IDS) {
            states.add(s);
        }

        stateSpinner = findViewById(R.id.stateSpinner);
        stateSpinner.setAdapter(new StatusArrayAdapter(this, states));

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
                List<ReportInfo> reports = deviceInfo.getReports();
                reports.sort(Comparator.comparingInt(reportInfo -> reportInfo.getReport().getId()));

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
                    .add(buildTutorialStep(reportThreadView, getString(R.string.device_info_tutorial_report_list), Gravity.BOTTOM))
                    .add(buildTutorialStep(reportForm, getString(R.string.device_info_tutorial_report_creation), Gravity.CENTER));

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

    public void createReport(View view) {
        int newState = (int)stateSpinner.getSelectedItem();

        if(newState >= 0) {
            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            String description = descriptionText.getText().toString().trim();
            String title = titleText.getText().toString().trim();

            if(title.length() > 0) {
                //ID = 0 means auto-generate ID
                Report report = new Report(0, preferences.getInt(Defaults.ID_PREFERENCE, -1), title, deviceId, hospitalId, newState, description, new Date());

                viewModel.createReport(report, preferences.getInt(Defaults.ID_PREFERENCE, -1));

                stateSpinner.setSelection(0);
                titleText.getText().clear();
                descriptionText.getText().clear();
            } else {
                titleText.setError(getString(R.string.empty_title));
            }
        } else {
            Toast.makeText(this.getApplicationContext(), getString(R.string.no_state_selected), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Adapter for displaying the device state spinner.
     */
    private static class StatusArrayAdapter extends ArrayAdapter<Integer> {

        private StatusArrayAdapter(Context context, List<Integer> values) {
            super(context, -1, values);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_status, parent, false);
            }

            ImageView statusImageView = convertView.findViewById(R.id.statusImageView);
            TextView statusTextView = convertView.findViewById(R.id.statusTextView);

            int state = getItem(position);

            if(state >= 0) {
                DeviceStateVisuals visuals = new DeviceStateVisuals(state, this.getContext());

                statusTextView.setText(visuals.getStateString());

                statusImageView.setImageDrawable(visuals.getStateIcon());
                statusImageView.setBackgroundColor(visuals.getBackgroundColor());

                statusImageView.setVisibility(View.VISIBLE);
            } else {
                statusTextView.setText(getContext().getString(R.string.select_state));
                statusImageView.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_status_short, parent, false);
            }

            ImageView statusImageView = convertView.findViewById(R.id.statusImageView);
            TextView statusTextView = convertView.findViewById(R.id.statusTextView);

            int state = getItem(position);

            if(state >= 0) {
                DeviceStateVisuals visuals = new DeviceStateVisuals(state, this.getContext());

                statusImageView.setImageDrawable(visuals.getStateIcon());
                statusImageView.setBackgroundColor(visuals.getBackgroundColor());

                statusTextView.setVisibility(View.GONE);
                statusImageView.setVisibility(View.VISIBLE);
            } else {
                statusTextView.setText(getContext().getString(R.string.select_state));
                statusTextView.setVisibility(View.VISIBLE);
                statusImageView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}


