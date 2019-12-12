package ngo.teog.swift.gui.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
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
import ngo.teog.swift.helpers.DeviceStateVisuals;
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

        EditText searchView = findViewById(R.id.search_view);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView locationView = findViewById(R.id.locationView);

        TextView mapButton = findViewById(R.id.map_button);

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
        if(item.getItemId() == R.id.advanded) {
            Intent intent = new Intent(this, AdvancedHospitalActivity.class);

            startActivity(intent);

            return true;
        } else {
            return super.onOptionsItemSelected(item, R.string.hospital_activity);
        }
    }

    private class ExpandableHospitalAdapter extends BaseExpandableListAdapter {
        private List<User> users = new ArrayList<>();
        private List<User> filteredUsers = new ArrayList<>();
        private List<DeviceInfo> deviceInfos = new ArrayList<>();
        private List<DeviceInfo> filteredDeviceInfos = new ArrayList<>();

        public void setUsers(List<User> users) {
            this.users = users;

            filteredUsers = new ArrayList<>(users);

            this.notifyDataSetChanged();
        }

        public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
            this.deviceInfos = deviceInfos;

            filteredDeviceInfos = new ArrayList<>(deviceInfos);

            this.notifyDataSetChanged();
        }

        public void filter(String searchString) {
            String matchingString = searchString.toLowerCase();

            filteredUsers = new ArrayList<>();
            filteredDeviceInfos = new ArrayList<>();

            //first filter users by name
            for (User user : users) {
                if (user.getName().toLowerCase().contains(matchingString)) {
                    filteredUsers.add(user);
                }
            }

            //now filter devices by type, model and manufacturer
            for (DeviceInfo deviceInfo : deviceInfos) {
                HospitalDevice device = deviceInfo.getDevice();

                String type = device.getType().toLowerCase();
                String manufacturer = device.getManufacturer().toLowerCase();
                String model = device.getModel().toLowerCase();

                if(type.contains(matchingString) || manufacturer.contains(matchingString) || model.contains(matchingString)) {
                    filteredDeviceInfos.add(deviceInfo);
                }
            }

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
                    return filteredUsers.size();
                case 1:
                    return filteredDeviceInfos.size();
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
                    return filteredUsers.get(childPosition);
                case 1:
                    return filteredDeviceInfos.get(childPosition);
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
                    countView.setText(Integer.toString(filteredUsers.size()));
                    break;
                case 1:
                    nameView.setText(R.string.hospital_devices_heading);
                    countView.setText(Integer.toString(filteredDeviceInfos.size()));
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

                    User user = filteredUsers.get(childPosition);

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

                    DeviceInfo deviceInfo = filteredDeviceInfos.get(childPosition);

                    if(deviceInfo != null) {
                        HospitalDevice device = deviceInfo.getDevice();

                        statusView.setText(device.getWard());

                        detailsView.setText(device.getManufacturer() + "\n" + device.getModel());

                        nameView.setText(device.getType());

                        if (deviceInfo.getReports().size() > 0) {
                            Report lastReport = deviceInfo.getReports().get(deviceInfo.getReports().size() - 1).getReport();

                            DeviceStateVisuals triple = new DeviceStateVisuals(lastReport.getCurrentState(), HospitalActivity.this);

                            imageView.setImageDrawable(triple.getStateIcon());
                            imageView.setBackgroundColor(triple.getBackgroundColor());
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
