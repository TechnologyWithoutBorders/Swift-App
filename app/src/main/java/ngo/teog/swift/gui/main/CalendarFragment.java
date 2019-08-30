package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.maintenance.SearchActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class CalendarFragment extends BaseFragment {

    DateFormat dateFormat = new SimpleDateFormat(Defaults.DATE_PATTERN);

    private static final int DAY_COUNT = 8;

    private ExpandableListView hospitalListView;

    @Inject
    ViewModelFactory viewModelFactory;

    private TodoViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_maintenance, container, false);

        hospitalListView = rootView.findViewById(R.id.maintenanceCalendar);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ExpandableHospitalAdapter adapter = new ExpandableHospitalAdapter();
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
            if(deviceInfos != null) {
                List<List<DeviceInfo>> dayList = new ArrayList<>(DAY_COUNT);

                //TODO wenn mans hier anders aufbaut und z.b. schon bearbeitete entfernt, wärs deutlich schneller

                for(int i = 0; i < DAY_COUNT; i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, i);

                    List<DeviceInfo> deviceInfoList = new ArrayList<>();

                    for(DeviceInfo deviceInfo : deviceInfos) {
                        HospitalDevice device = deviceInfo.getDevice();
                        List<ReportInfo> reports = deviceInfo.getReports();

                        if(reports.size() > 0) {
                            Report lastReport = reports.get(reports.size() - 1).getReport();
                            int currentState = lastReport.getCurrentState();

                            if (currentState == 0) {
                                int maintenanceInterval = device.getMaintenanceInterval();
                                Date created = lastReport.getCreated();

                                Date nextMaintenance = new Date(created.getTime() + maintenanceInterval * 7 * 24 * 60 * 60);
                                Date calendarDate = cal.getTime();

                                //Dieser Vergleich könnte problematisch sein
                                if (dateFormat.format(nextMaintenance).equals(dateFormat.format(calendarDate))) {
                                    deviceInfoList.add(deviceInfo);
                                }
                            }
                        }
                    }

                    dayList.add(deviceInfoList);
                }

                Collections.reverse(dayList);

                adapter.setDeviceInfos(dayList);
            }
        });
    }

    private class ExpandableHospitalAdapter extends BaseExpandableListAdapter {

        private List<List<DeviceInfo>> deviceInfos = new ArrayList<List<DeviceInfo>>(DAY_COUNT);

        private ExpandableHospitalAdapter() {
            for(int i = 0; i < DAY_COUNT; i++) {
                deviceInfos.add(new ArrayList<>());
            }
        }

        private void setDeviceInfos(List<List<DeviceInfo>> deviceInfos) {
            this.deviceInfos = deviceInfos;
            this.notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return DAY_COUNT;
        }

        @Override
        public int getChildrenCount(int i) {
            return deviceInfos.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return i;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return deviceInfos.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.header_hospital, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView countView = convertView.findViewById(R.id.countView);

            if(groupPosition == 0) {
                nameView.setText(getText(R.string.calendar_today_tag));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("E yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, groupPosition);

                nameView.setText(sdf.format(c.getTime()));
            }

            countView.setText(Integer.toString(deviceInfos.get(groupPosition).size()));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null || (int)convertView.getTag() != groupPosition) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_todo, parent, false);
                convertView.setTag(groupPosition);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView statusView = convertView.findViewById(R.id.statusView);
            ImageView imageView = convertView.findViewById(R.id.imageView);

            DeviceInfo deviceInfo = deviceInfos.get(groupPosition).get(childPosition);

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();
                Report lastReport = deviceInfo.getReports().get(deviceInfo.getReports().size()-1).getReport();

                nameView.setText(device.getType());

                String dateString = dateFormat.format(lastReport.getCreated());
                dateView.setText(dateString);

                DeviceState triple = DeviceState.buildState(lastReport.getCurrentState(), getContext());

                statusView.setText(triple.getStatestring());

                imageView.setImageDrawable(triple.getStateicon());
                imageView.setBackgroundColor(triple.getBackgroundcolor());
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public int getChildTypeCount() {
            return 1;
        }

        @Override
        public int getGroupTypeCount() {
            return DAY_COUNT;
        }

        @Override
        public int getGroupType(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            return groupPosition;
        }
    }
}
