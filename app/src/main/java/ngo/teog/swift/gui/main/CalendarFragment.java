package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Part of the main activity, shows how much time is left before devices should be serviced again.
 * @author nitelow
 */
public class CalendarFragment extends Fragment {

    private ListView hospitalListView;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_maintenance, container, false);

        hospitalListView = rootView.findViewById(R.id.maintenanceList);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        CustomSimpleArrayAdapter adapter = new CustomSimpleArrayAdapter(getContext());
        hospitalListView.setAdapter(adapter);

        hospitalListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
            intent.putExtra(ResourceKeys.DEVICE_ID, ((MaintenanceInfo)adapterView.getItemAtPosition(i)).getDeviceInfo().getDevice().getId());
            startActivity(intent);
        });

        DaggerAppComponent.builder()
                .appModule(new AppModule(requireActivity().getApplication()))
                .roomModule(new RoomModule(requireActivity().getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.requireContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        MainViewModel viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel.init(id);
        viewModel.getDeviceInfos().observe(this.getViewLifecycleOwner(), dbDeviceInfos -> {
            if(dbDeviceInfos != null && !dbDeviceInfos.isEmpty()) {
                //make copies of device infos as data is shared between the fragments
                List<DeviceInfo> deviceInfos = new ArrayList<>(dbDeviceInfos);

                adapter.clear();
                List<MaintenanceInfo> values = new ArrayList<>();

                Date now = new Date();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    if(!deviceInfo.getReports().isEmpty()) {
                        //copy report list as well and assign it to the device info
                        List<ReportInfo> reversedReportInfos = new ArrayList<>(deviceInfo.getReports());
                        reversedReportInfos.sort((first, second) -> second.getReport().getId() - first.getReport().getId());
                        deviceInfo.setReports(reversedReportInfos);

                        ReportInfo latestReportInfo = reversedReportInfos.get(0);

                        int newestState = latestReportInfo.getReport().getCurrentState();

                        HospitalDevice device = deviceInfo.getDevice();

                        //ignore devices in the to-do list and salvage devices
                        if(newestState != DeviceState.BROKEN && newestState != DeviceState.MAINTENANCE && newestState != DeviceState.IN_PROGRESS && newestState != DeviceState.SALVAGE) {
                            //look for last maintenance/repair or creation
                            Date lastMaintenance = null;

                            for(ReportInfo info : reversedReportInfos) {
                                Report report = info.getReport();

                                int previousState = report.getCurrentState();

                                if (previousState == DeviceState.MAINTENANCE || previousState == DeviceState.BROKEN) {
                                    break;
                                }

                                lastMaintenance = report.getCreated();
                            }

                            if (lastMaintenance != null) {
                                int daysLeft = (device.getMaintenanceInterval()*7)-((int) ((now.getTime() - lastMaintenance.getTime()) / 1000 / 60 / 60 / 24));
                                // TODO:  if abfrage ob days left negativ oder positiv ist -> in zwei listen eintragen, überfällige Zählen
                                values.add(new MaintenanceInfo(deviceInfo, daysLeft));
                            }
                        }
                    }
                }

                values.sort(Comparator.comparingInt(MaintenanceInfo::getDaysLeft));

                adapter.addAll(values);
            }
        });
    }

    private class CustomSimpleArrayAdapter extends ArrayAdapter<MaintenanceInfo> {
        private CustomSimpleArrayAdapter(Context context) {
            super(context, -1);
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

                int daysLeft = maintenanceInfo.getDaysLeft();

                String dateString = daysLeft + " " + getString(R.string.days_left);

                if(daysLeft < 0) {
                    dateString = Math.abs(daysLeft) + " " + getString(R.string.days_over);
                }

                statusView.setText(dateString);

                nameView.setText(device.getType());

                if(daysLeft <= 0) {
                    maintenanceBar.setProgress(0);
                    maintenanceBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                } else {
                    maintenanceBar.setProgress((int)(((float)maintenanceInfo.getDaysLeft()/(device.getMaintenanceInterval()*7))*100));
                    maintenanceBar.getProgressDrawable().setColorFilter(null);
                }
            }

            return convertView;
        }
    }

    private static class MaintenanceInfo {
        private final DeviceInfo deviceInfo;
        private final int daysLeft;

        private MaintenanceInfo(DeviceInfo deviceInfo, int daysLeft) {
            this.deviceInfo = deviceInfo;
            this.daysLeft = daysLeft;
        }

        private DeviceInfo getDeviceInfo() {
            return deviceInfo;
        }

        private int getDaysLeft() {
            return daysLeft;
        }
    }
}
