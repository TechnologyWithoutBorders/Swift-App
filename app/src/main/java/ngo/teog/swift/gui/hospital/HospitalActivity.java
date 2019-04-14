package ngo.teog.swift.gui.hospital;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.UserInfoActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class HospitalActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private HospitalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        LinearLayout contentView = findViewById(R.id.contentView);
        final ExpandableListView hospitalListView = findViewById(R.id.hospitalList);

        hospitalListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                switch(groupPosition) {
                    case 0:
                        Intent intent = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                        intent.putExtra("user", (User)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition));
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                        intent2.putExtra("device", (HospitalDevice)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition));
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView locationView = findViewById(R.id.locationView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HospitalViewModel.class);
        viewModel.init(id);
        viewModel.getHospital().observe(this, hospital -> {
            if(hospital != null) {
                nameView.setText(hospital.getName());
                locationView.setText(hospital.getLocation());
            }
        });
        viewModel.getUsers().observe(this, users -> {
           if(users != null) {
               ExpandableHospitalAdapter adapter = new ExpandableHospitalAdapter(users);
               hospitalListView.setAdapter(adapter);
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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.hospital_activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ExpandableHospitalAdapter extends BaseExpandableListAdapter {
        private List<User> users;

        private ExpandableHospitalAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int i) {
            switch(i) {
                case 0:
                    return users.size();
                default:
                    return 0;
            }
        }

        @Override
        public Object getGroup(int i) {
            switch(i) {
                case 0:
                    return "Members";
                default:
                    return null;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case 0:
                    return users.get(childPosition);
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
                    nameView.setText("Members");
                    countView.setText(Integer.toString(users.size()));
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
