package ngo.teog.swift.gui.maintenance;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.main.CalendarViewModel;
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

public class SearchActivity extends BaseActivity {

    private static final int DAY_COUNT = 7;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintenance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.maintenance_info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                LayoutInflater inflater = (LayoutInflater) SearchActivity.this
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
                LayoutInflater inflater = (LayoutInflater) SearchActivity.this
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

                String dateString = Defaults.DATE_FORMAT.format(lastReport.getCreated());
                dateView.setText(dateString);

                DeviceState triple = DeviceState.buildState(lastReport.getCurrentState(), SearchActivity.this);

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
