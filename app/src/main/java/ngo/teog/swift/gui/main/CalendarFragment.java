package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class CalendarFragment extends Fragment {

    private ListView hospitalListView;

    private List<MaintenanceInfo> values = new ArrayList<>();

    @Inject
    ViewModelFactory viewModelFactory;

    private TodoViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_maintenance, container, false);

        hospitalListView = rootView.findViewById(R.id.maintenanceList);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        CustomSimpleArrayAdapter adapter = new CustomSimpleArrayAdapter(getContext(), values);
        hospitalListView.setAdapter(adapter);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        CalendarViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(CalendarViewModel.class);
        viewModel.init(id);
        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null && deviceInfos.size() > 0) {
                adapter.clear();

                Date now = new Date();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    List<ReportInfo> reports = deviceInfo.getReports();
                    Collections.reverse(reports);

                    int newestState = reports.get(0).getReport().getCurrentState();
                    Date lastMaintenance = null;

                    //ignore devices in the to-do list and salvage devices
                    if(newestState != DeviceState.BROKEN && newestState != DeviceState.MAINTENANCE && newestState != DeviceState.IN_PROGRESS && newestState != DeviceState.SALVAGE) {
                        //look for last maintenance/repair or creation
                        for(ReportInfo info : reports) {
                            Report report = info.getReport();
                            int previousState = report.getPreviousState();
                            int currentState = report.getCurrentState();

                            if(previousState == DeviceState.MAINTENANCE || previousState == DeviceState.BROKEN || (previousState == DeviceState.WORKING && currentState == DeviceState.WORKING)) {
                                lastMaintenance = report.getCreated();
                                break;
                            }
                        }

                        if(lastMaintenance != null) {
                            int daysOver = (int)((now.getTime() - lastMaintenance.getTime())/1000/60/60/24);

                            adapter.add(new MaintenanceInfo(deviceInfo, daysOver));
                        }
                    }
                }

                /*Collections.sort(deviceInfos, (first, second) -> {
                    List<ReportInfo> firstReports = first.getReports();
                    List<ReportInfo> secondReports = second.getReports();

                    if(firstReports.size() > 0 && secondReports.size() > 0) {
                        int firstState = firstReports.get(0).getReport().getCurrentState();
                        int secondState = secondReports.get(0).getReport().getCurrentState();

                        return (firstState-secondState)*-1;
                    } else {
                        return 0;
                    }
                });*/
            }
        });
    }

    private class CustomSimpleArrayAdapter extends ArrayAdapter<MaintenanceInfo> {
        private CustomSimpleArrayAdapter(Context context, List<MaintenanceInfo> values) {
            super(context, -1, values);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_calendar, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView statusView = convertView.findViewById(R.id.statusView);
            ProgressBar maintenanceBar = convertView.findViewById(R.id.maintenance_bar);

            MaintenanceInfo maintenanceInfo = this.getItem(position);

            if(maintenanceInfo != null) {
                DeviceInfo deviceInfo = maintenanceInfo.getDeviceInfo();

                HospitalDevice device = deviceInfo.getDevice();

                int daysLeft = device.getMaintenanceInterval()*7-maintenanceInfo.getDaysOver();

                String dateString = daysLeft + " days left";

                statusView.setText(dateString);

                nameView.setText(device.getType());

                if(daysLeft <= 0) {
                    maintenanceBar.setProgress(0);
                    maintenanceBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                } else {
                    maintenanceBar.setProgress(100-(int)(((float)maintenanceInfo.getDaysOver()/(device.getMaintenanceInterval()*7))*100));
                }
            }

            return convertView;
        }
    }

    private class MaintenanceInfo {
        private DeviceInfo deviceInfo;
        private int daysOver;

        private MaintenanceInfo(DeviceInfo deviceInfo, int daysOver) {
            this.deviceInfo = deviceInfo;
            this.daysOver = daysOver;
        }

        private DeviceInfo getDeviceInfo() {
            return deviceInfo;
        }

        private int getDaysOver() {
            return daysOver;
        }
    }
}
