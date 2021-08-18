package ngo.teog.swift.gui.reportInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
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

    //private DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        //int reportId = intent.getIntExtra(ResourceKeys.REPORT_ID, -1);TODO

        TextView titleView = findViewById(R.id.title_view);
        RecyclerView reportThreadView = findViewById(R.id.report_thread_view);

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
            //this.deviceInfo = deviceInfo;TODO

            if(deviceInfo != null) {
                titleView.setText(deviceInfo.getDevice().getType());

                ReportThreadAdapter adapter = new ReportThreadAdapter(this, deviceInfo.getReports());
                reportThreadView.setAdapter(adapter);
                reportThreadView.setLayoutManager(new LinearLayoutManager(this));
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
                return new SentMessageHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_thread_other, parent, false);
                return new ReceivedMessageHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Report report = reportList.get(position).getReport();

            switch(holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(report);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(report);
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            private final TextView titleView;

            ReceivedMessageHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.title_view);
            }

            void bind(Report report) {
                titleView.setText(report.getTitle());

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
            private final TextView titleView;

            SentMessageHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.title_view);
            }

            void bind(Report report) {
                titleView.setText(report.getTitle());
            }
        }
    }
}


