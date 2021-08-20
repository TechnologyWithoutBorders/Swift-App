package ngo.teog.swift.gui.reportInfo;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
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

    @Inject
    ViewModelFactory viewModelFactory;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        TextView titleView = findViewById(R.id.title_view);
        RecyclerView reportThreadView = findViewById(R.id.report_thread_view);

        Button reportCreationButton = findViewById(R.id.reportCreationButton);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        ReportInfoViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(ReportInfoViewModel.class);
        viewModel.init(userId, deviceId);

        viewModel.getDeviceInfo().observe(this, deviceInfo -> {
            if(deviceInfo != null) {
                reportCreationButton.setOnClickListener((view) -> {
                    Intent reportIntent = new Intent(ReportInfoActivity.this, ReportCreationActivity.class);
                    reportIntent.putExtra(ResourceKeys.HOSPITAL_ID, deviceInfo.getHospital().getId());
                    reportIntent.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                    startActivity(reportIntent);
                });

                titleView.setText(deviceInfo.getDevice().getType());

                ReportThreadAdapter adapter = new ReportThreadAdapter(this, deviceInfo.getReports());
                reportThreadView.setAdapter(adapter);
                reportThreadView.setLayoutManager(new LinearLayoutManager(this));
                if(adapter.getItemCount() > 0) {
                    reportThreadView.scrollToPosition(adapter.getItemCount() - 1);
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

    /*private void shareReport(ReportInfo reportInfo) {//TODO
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
            /*FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()TODO
                    .add(buildTutorialStep(stateChange, getString(R.string.report_tutorial_state_change), Gravity.CENTER))
                    .add(buildTutorialStep(descriptionLayout, getString(R.string.report_tutorial_description), Gravity.TOP));

            tutorialQueue.show();*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReportThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_MESSAGE_SENT = 0;//TODO rename
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

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
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_MESSAGE_SENT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_thread_own, parent, false);
                return new SentMessageHolder(this.context, view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_thread_other, parent, false);
                return new ReceivedMessageHolder(this.context, view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ReportInfo reportInfo = reportList.get(position);

            switch(holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(reportInfo);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(reportInfo);
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            private final ImageView toState;
            private final TextView dateView, authorView, titleView, descriptionView;

            private final Context context;

            ReceivedMessageHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;

                toState = itemView.findViewById(R.id.toState);
                dateView = itemView.findViewById(R.id.date_view);
                authorView = itemView.findViewById(R.id.author_view);
                titleView = itemView.findViewById(R.id.title_view);
                descriptionView = itemView.findViewById(R.id.description_view);
            }

            void bind(ReportInfo reportInfo) {
                Report report = reportInfo.getReport();

                DeviceStateVisuals stateVisuals = new DeviceStateVisuals(report.getCurrentState(),this.context);

                toState.setImageDrawable(stateVisuals.getStateIcon());
                toState.setColorFilter(stateVisuals.getBackgroundColor());

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN, Locale.getDefault());
                dateView.setText(dateFormat.format(report.getCreated()));

                authorView.setText(reportInfo.getAuthor().getName() + ":");
                titleView.setText(report.getTitle());
                descriptionView.setText(report.getDescription());

                /*
                DeviceStateVisuals currentStateInfo = new DeviceStateVisuals(report.getCurrentState(), this);
                toState.setImageDrawable(currentStateInfo.getStateIcon());
                toState.setBackgroundColor(currentStateInfo.getBackgroundColor());
                toStateText.setText(currentStateInfo.getStateString());
                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PATTERN, Locale.getDefault());
                dateView.setText(dateFormat.format(report.getCreated()));
                authorView.setText(author.getName());
                 */
            }
        }

        private class SentMessageHolder extends RecyclerView.ViewHolder {
            private final ImageView toState;
            private final TextView dateView, titleView, descriptionView;

            private final Context context;

            SentMessageHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;

                toState = itemView.findViewById(R.id.toState);
                dateView = itemView.findViewById(R.id.date_view);
                titleView = itemView.findViewById(R.id.title_view);
                descriptionView = itemView.findViewById(R.id.description_view);
            }

            void bind(ReportInfo reportInfo) {
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


