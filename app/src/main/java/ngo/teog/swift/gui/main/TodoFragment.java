package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class TodoFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private TodoViewModel viewModel;

    private CustomSimpleArrayAdapter adapter;

    private List<DeviceInfo> values = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.maintenanceList);

        adapter = new CustomSimpleArrayAdapter(getContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
            intent.putExtra(ResourceKeys.DEVICE_ID, ((DeviceInfo)adapterView.getItemAtPosition(i)).getDevice().getId());
            startActivity(intent);
        });

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    swipeRefreshLayout.setRefreshing(false);
                    refresh();
                }
        );

        DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(TodoViewModel.class);
        viewModel.init(id);
        viewModel.getDeviceInfos().observe(this.getViewLifecycleOwner(), deviceInfos -> {
            if(deviceInfos != null && deviceInfos.size() > 0) {
                this.values = deviceInfos;
                adapter.clear();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    List<ReportInfo> reports = deviceInfo.getReports();
                    Collections.reverse(reports);
                }

                Collections.sort(deviceInfos, (first, second) -> {
                    List<ReportInfo> firstReports = first.getReports();
                    List<ReportInfo> secondReports = second.getReports();

                    if(firstReports.size() > 0 && secondReports.size() > 0) {
                        int firstState = firstReports.get(0).getReport().getCurrentState();
                        int secondState = secondReports.get(0).getReport().getCurrentState();

                        return (firstState-secondState)*-1;
                    } else {
                        return 0;
                    }
                });

                for(DeviceInfo deviceInfo : deviceInfos) {
                    List<ReportInfo> reportInfos = deviceInfo.getReports();

                    if(reportInfos.size() > 0) {
                        int currentState = reportInfos.get(0).getReport().getCurrentState();

                        if(currentState == DeviceState.MAINTENANCE || currentState == DeviceState.BROKEN || currentState == DeviceState.IN_PROGRESS) {
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

        refresh();
    }

    private void refresh() {
        SharedPreferences preferences = this.getContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel.refreshHospital(userId);
    }

    private class CustomSimpleArrayAdapter extends ArrayAdapter<DeviceInfo> {
        private CustomSimpleArrayAdapter(Context context, List<DeviceInfo> values) {
            super(context, -1, values);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_todo, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView statusView = convertView.findViewById(R.id.statusView);
            ImageView imageView = convertView.findViewById(R.id.imageView);
            TextView detailView = convertView.findViewById(R.id.detailView);

            DeviceInfo deviceInfo = this.getItem(position);

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();
                Report lastReport = deviceInfo.getReports().get(0).getReport();

                nameView.setText(device.getType());

                long now = new Date().getTime();
                Date reportDate = lastReport.getCreated();
                String dateString = (now-reportDate.getTime())/1000/60/60/24 + " d";
                dateView.setText(dateString);

                detailView.setText(device.getManufacturer() + "\n" + device.getModel());

                DeviceStateVisuals triple = new DeviceStateVisuals(lastReport.getCurrentState(), this.getContext());

                statusView.setText(device.getLocation());
                imageView.setImageDrawable(triple.getStateIcon());
                imageView.setBackgroundColor(triple.getBackgroundColor());
            }

            return convertView;
        }
    }
}
