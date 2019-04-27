package ngo.teog.swift.gui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.hospital.HospitalViewModel;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.UpdateWorker;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class TodoFragment extends BaseFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private TodoViewModel viewModel;

    private CustomSimpleArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    private List<DeviceInfo> values = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = view.findViewById(R.id.maintenanceList);

        progressBar = view.findViewById(R.id.progressBar);

        adapter = new CustomSimpleArrayAdapter(getContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
                intent.putExtra("device", (DeviceInfo)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        /*final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                    refresh();
                }
            }
        );*/

        DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoViewModel.class);
        viewModel.init(id);
        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null && deviceInfos.size() > 0) {
                this.values = deviceInfos;
                adapter.clear();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    List<ReportInfo> reports = deviceInfo.getReports();
                    Collections.reverse(reports);
                }

                Collections.sort(deviceInfos, new Comparator<DeviceInfo>() {
                    @Override
                    public int compare(DeviceInfo first, DeviceInfo second) {
                        List<ReportInfo> firstReports = first.getReports();
                        List<ReportInfo> secondReports = second.getReports();

                        if(firstReports.size() > 0 && secondReports.size() > 0) {
                            int firstState = firstReports.get(0).getReport().getCurrentState();
                            int secondState = secondReports.get(0).getReport().getCurrentState();

                            return (firstState-secondState)*-1;
                        } else {
                            return 0;
                        }
                    }
                });

                for(DeviceInfo deviceInfo : deviceInfos) {
                    List<ReportInfo> reportInfos = deviceInfo.getReports();

                    if(reportInfos.size() > 0) {
                        if(reportInfos.get(0).getReport().getCurrentState() == 1 || reportInfos.get(0).getReport().getCurrentState() == 2) {
                            adapter.add(deviceInfo);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //refresh();
    }

    /*private void refresh() {
        Constraints updateConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkManager.getInstance().cancelAllWorkByTag("update_todo");

        PeriodicWorkRequest updateWork = new PeriodicWorkRequest.Builder(UpdateWorker.class, 6, TimeUnit.HOURS)
                .addTag("update_todo")
                .setConstraints(updateConstraints)
                .build();

        WorkManager.getInstance().enqueue(updateWork);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

            RequestFactory.DeviceListRequest request = new RequestFactory().createTodoListRequest(getContext(), progressBar, listView, adapter);

            queue.add(request);
        }
    }*/

    private class CustomSimpleArrayAdapter extends ArrayAdapter<DeviceInfo> {
        private CustomSimpleArrayAdapter(Context context, List<DeviceInfo> values) {
            super(context, -1, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_todo, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView statusView = convertView.findViewById(R.id.statusView);
            ImageView imageView = convertView.findViewById(R.id.imageView);

            DeviceInfo deviceInfo = this.getItem(position);

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();
                Report lastReport = deviceInfo.getReports().get(0).getReport();

                nameView.setText(device.getType());

                String dateString = Defaults.DATE_FORMAT.format(lastReport.getCreated());
                dateView.setText(dateString);

                DeviceState triple = DeviceState.buildState(lastReport.getCurrentState(), this.getContext());

                statusView.setText(device.getWard());
                imageView.setImageDrawable(triple.getStateicon());
                imageView.setBackgroundColor(triple.getBackgroundcolor());
            }

            return convertView;
        }
    }
}
