package ngo.teog.swift.gui.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class HospitalActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        final ExpandableListView hospitalListView = findViewById(R.id.hospitalList);

        ExpandableHospitalAdapter adapter = new ExpandableHospitalAdapter();
        hospitalListView.setAdapter(adapter);

        hospitalListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            switch(groupPosition) {
                case 0:
                    Intent intent = new Intent(HospitalActivity.this, UserInfoActivity.class);
                    intent.putExtra(ResourceKeys.USER_ID, ((User)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition)).getId());
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent2 = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                    intent2.putExtra(ResourceKeys.DEVICE_ID, ((DeviceInfo)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition)).getDevice().getId());
                    startActivity(intent2);
                    break;
            }

            return false;
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView locationView = findViewById(R.id.locationView);

        ImageView mapButton = findViewById(R.id.mapButton);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        HospitalViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(HospitalViewModel.class);
        viewModel.init(id);
        viewModel.getHospital().observe(this, hospital -> {
            if(hospital != null) {
                nameView.setText(hospital.getName());
                locationView.setText(hospital.getLocation());

                mapButton.setOnClickListener(view -> {
                    float latitude = hospital.getLatitude();
                    float longitude = hospital.getLongitude();

                    Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + hospital.getName() + ")");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    HospitalActivity.this.startActivity(intent);
                });
            }
        });

        viewModel.getUsers().observe(this, users -> {
           if(users != null) {
               Collections.sort(users, (first, second) -> first.getName().compareTo(second.getName()));
               adapter.setUsers(users);
           }
        });

        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null) {
                Collections.sort(deviceInfos, (first, second) -> first.getDevice().getType().compareTo(second.getDevice().getType()));
                adapter.setDeviceInfos(deviceInfos);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_hospital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.hospital_activity);
    }

    private class ExpandableHospitalAdapter extends BaseExpandableListAdapter {
        private List<User> users = new ArrayList<>();
        private List<DeviceInfo> deviceInfos = new ArrayList<>();

        public void setUsers(List<User> users) {
            this.users = users;
            this.notifyDataSetChanged();
        }

        public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
            this.deviceInfos = deviceInfos;
            this.notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch(i) {
                case 0:
                    return users.size();
                case 1:
                    return deviceInfos.size();
                default:
                    return 0;
            }
        }

        @Override
        public Object getGroup(int i) {
            return i;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case 0:
                    return users.get(childPosition);
                case 1:
                    return deviceInfos.get(childPosition);
                default:
                    return null;
            }
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
                LayoutInflater inflater = (LayoutInflater) HospitalActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.header_hospital, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView countView = convertView.findViewById(R.id.countView);

            switch(groupPosition) {
                case 0:
                    nameView.setText(R.string.hospital_members_heading);
                    countView.setText(Integer.toString(users.size()));
                    break;
                case 1:
                    nameView.setText(R.string.hospital_devices_heading);
                    countView.setText(Integer.toString(deviceInfos.size()));
                    break;
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            switch(groupPosition) {
                case 0:
                    if(convertView == null || (int)convertView.getTag() != groupPosition) {
                        LayoutInflater inflater = (LayoutInflater) HospitalActivity.this
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.row_members, parent, false);
                        convertView.setTag(groupPosition);
                    }

                    User user = users.get(childPosition);

                    if(user != null) {
                        TextView nameView = convertView.findViewById(R.id.nameView);
                        TextView positionView = convertView.findViewById(R.id.positionView);

                        nameView.setText(user.getName());
                        positionView.setText(user.getPosition());
                    }
                    break;
                case 1:
                    if(convertView == null || (int)convertView.getTag() != groupPosition) {
                        LayoutInflater inflater = (LayoutInflater) HospitalActivity.this
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.row_todo, parent, false);
                        convertView.setTag(groupPosition);
                    }

                    TextView nameView = convertView.findViewById(R.id.nameView);
                    TextView statusView = convertView.findViewById(R.id.statusView);
                    ImageView imageView = convertView.findViewById(R.id.imageView);
                    TextView detailsView = convertView.findViewById(R.id.detailView);

                    DeviceInfo deviceInfo = deviceInfos.get(childPosition);

                    if(deviceInfo != null) {
                        HospitalDevice device = deviceInfo.getDevice();

                        statusView.setText(device.getWard());

                        detailsView.setText(device.getManufacturer() + "\n" + device.getModel());

                        nameView.setText(device.getType());

                        if (deviceInfo.getReports().size() > 0) {
                            Report lastReport = deviceInfo.getReports().get(deviceInfo.getReports().size() - 1).getReport();

                            DeviceState triple = DeviceState.buildState(lastReport.getCurrentState(), HospitalActivity.this);

                            imageView.setImageDrawable(triple.getStateicon());
                            imageView.setBackgroundColor(triple.getBackgroundcolor());
                        }
                    }

                    break;
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public int getChildTypeCount() {
            return 2;
        }

        @Override
        public int getGroupTypeCount() {
            return 2;
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
