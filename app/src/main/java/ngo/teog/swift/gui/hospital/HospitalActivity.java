package ngo.teog.swift.gui.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
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
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Gives a quick overview about a hospital.
 * @author nitelow
 */
public class HospitalActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private ExpandableListView hospitalListView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        LinearLayout contentView = findViewById(R.id.content_view);

        LinearLayout hospitalInfo = findViewById(R.id.hospital_info);
        hospitalListView = findViewById(R.id.hospitalList);

        ExpandableHospitalAdapter adapter = new ExpandableHospitalAdapter();
        hospitalListView.setAdapter(adapter);

        hospitalListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            switch(groupPosition) {
                case ExpandableHospitalAdapter.DEVICES_GROUP:
                    Intent intent2 = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                    intent2.putExtra(ResourceKeys.DEVICE_ID, ((DeviceInfo)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition)).getDevice().getId());
                    startActivity(intent2);
                    break;
                case ExpandableHospitalAdapter.USERS_GROUP:
                    Intent intent = new Intent(HospitalActivity.this, UserInfoActivity.class);
                    intent.putExtra(ResourceKeys.USER_ID, ((User)hospitalListView.getExpandableListAdapter().getChild(groupPosition, childPosition)).getId());
                    startActivity(intent);
                    break;
            }

            return false;
        });

        searchView = findViewById(R.id.search_view);

        Transition transition = new AutoTransition()
                .setDuration(200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    TransitionManager.beginDelayedTransition(contentView, transition);
                    hospitalInfo.setVisibility(View.VISIBLE);
                } else {
                    TransitionManager.beginDelayedTransition(contentView, transition);
                    hospitalInfo.setVisibility(View.GONE);
                }

                adapter.filter(newText);

                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                TransitionManager.beginDelayedTransition(contentView, transition);
                hospitalInfo.setVisibility(View.GONE);

                hospitalListView.expandGroup(ExpandableHospitalAdapter.DEVICES_GROUP);
                hospitalListView.expandGroup(ExpandableHospitalAdapter.USERS_GROUP);
            }
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView locationView = findViewById(R.id.locationView);

        TextView mapButton = findViewById(R.id.map_button);

        //TODO in Methode ausgliedern

        DeviceStateVisuals workingParams = new DeviceStateVisuals(DeviceState.WORKING, this);
        TextView workingCounter = findViewById(R.id.working_count);
        this.setStateImage(workingCounter, workingParams);

        DeviceStateVisuals maintenanceParams = new DeviceStateVisuals(DeviceState.MAINTENANCE, this);
        TextView maintenanceCounter = findViewById(R.id.maintenance_count);
        this.setStateImage(maintenanceCounter, maintenanceParams);

        DeviceStateVisuals repairParams = new DeviceStateVisuals(DeviceState.BROKEN, this);
        TextView repairCounter = findViewById(R.id.repair_count);
        this.setStateImage(repairCounter, repairParams);

        DeviceStateVisuals progressParams = new DeviceStateVisuals(DeviceState.IN_PROGRESS, this);
        TextView progressCounter = findViewById(R.id.in_progress_count);
        this.setStateImage(progressCounter, progressParams);

        DeviceStateVisuals brokenParams = new DeviceStateVisuals(DeviceState.SALVAGE, this);
        TextView brokenCounter = findViewById(R.id.broken_count);
        this.setStateImage(brokenCounter, brokenParams);

        DeviceStateVisuals limitedParams = new DeviceStateVisuals(DeviceState.LIMITATIONS, this);
        TextView limitedCounter = findViewById(R.id.limited_count);
        this.setStateImage(limitedCounter, limitedParams);

        TextView overdueDevices = findViewById(R.id.amountOfOverdueDevices);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        HospitalViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(HospitalViewModel.class);
        viewModel.init(id).observe(this, observable -> viewModel.refreshDeviceInfos());
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
               Collections.sort(users, (first, second) -> first.getName().toLowerCase().compareTo(second.getName().toLowerCase()));
               adapter.setUsers(users);
           }
        });

        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null) {
                int[] stateCounters = {0, 0, 0, 0, 0, 0};

                Date now = new Date();

                int overdueDeviceCount = 0;

                for(DeviceInfo deviceInfo : deviceInfos) {
                    ReportInfo latestReportInfo = deviceInfo.getReports().get(deviceInfo.getReports().size()-1);

                    int newestState = latestReportInfo.getReport().getCurrentState();

                    stateCounters[newestState]++;

                    HospitalDevice device = deviceInfo.getDevice();

                    if(newestState != DeviceState.BROKEN && newestState != DeviceState.MAINTENANCE && newestState != DeviceState.IN_PROGRESS && newestState != DeviceState.SALVAGE) {
                        //look for last maintenance/repair or creation
                        Date lastMaintenance = null;

                        List<ReportInfo> reversedReportInfos = deviceInfo.getReports();
                        Collections.reverse(reversedReportInfos);

                        for(ReportInfo reportInfo : reversedReportInfos) {
                            Report report = reportInfo.getReport();

                            int previousState = report.getCurrentState();

                            if(previousState == DeviceState.MAINTENANCE || previousState == DeviceState.BROKEN) {
                                break;
                            }

                            lastMaintenance = report.getCreated();
                        }

                        if(lastMaintenance != null) {
                            int daysLeft = (device.getMaintenanceInterval()*7)-((int) ((now.getTime() - lastMaintenance.getTime()) / 1000 / 60 / 60 / 24));

                            if(daysLeft <= 0){
                                overdueDeviceCount++;
                            }
                        }
                    }
                }

                overdueDevices.setText(getResources().getQuantityString(R.plurals.hospital_info_overdue_devices, overdueDeviceCount, overdueDeviceCount));

                workingCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.WORKING]));
                maintenanceCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.MAINTENANCE]));
                repairCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.BROKEN]));
                progressCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.IN_PROGRESS]));
                brokenCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.SALVAGE]));
                limitedCounter.setText(String.format(Locale.ROOT, "%d", stateCounters[DeviceState.LIMITATIONS]));

                Collections.sort(deviceInfos, (first, second) -> first.getDevice().getType().toLowerCase().compareTo(second.getDevice().getType().toLowerCase()));
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
        if(item.getItemId() == R.id.portation) {
            Intent intent = new Intent(this, PortationActivity.class);

            startActivity(intent);

            return true;
        } else if(item.getItemId() == R.id.info) {
            //Show tutorial
            FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                    .add(buildTutorialStep(findViewById(R.id.device_state_overview), getString(R.string.hospital_info_tutorial_state_overview), Gravity.CENTER))
                    .add(buildTutorialStep(hospitalListView, getString(R.string.hospital_info_tutorial_asset_list), Gravity.TOP))
                    .add(buildTutorialStep(searchView, getString(R.string.hospital_info_tutorial_filter), Gravity.CENTER));

            tutorialQueue.show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setStateImage(TextView stateView, DeviceStateVisuals visuals) {
        Drawable stateIcon = visuals.getStateIcon();
        stateIcon.setColorFilter(new PorterDuffColorFilter(visuals.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP));
        stateIcon.setBounds(0, 0, 40, 40);
        stateView.setCompoundDrawables(null, stateIcon, null, null);
    }

    /**
     * Starts a system dialing activity using the given phone number.
     * @param phoneNumber number to call
     */
    private void invokeCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(Defaults.URI_TEL_PREFIX + phoneNumber));
        startActivity(dialIntent);
    }

    private class ExpandableHospitalAdapter extends BaseExpandableListAdapter {
        private static final int DEVICES_GROUP = 0;
        private static final int USERS_GROUP = 1;

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
            List<DeviceInfo> deviceInfoCopy = new ArrayList<>(deviceInfos);

            for(DeviceInfo deviceInfo : deviceInfoCopy) {
                List<ReportInfo> reports = deviceInfo.getReports();

                Collections.sort(reports, (first, second) -> second.getReport().getId()-first.getReport().getId());
            }

            this.deviceInfos = deviceInfoCopy;

            filteredDeviceInfos = new ArrayList<>(deviceInfos);

            this.notifyDataSetChanged();
        }

        /**
         * Fills the global filtered collections depending on the given search string.
         * @param searchString text the user searched for
         */
        public void filter(String searchString) {
            String matchingString = searchString.toLowerCase();

            List<PrioUser> prioUsers = new ArrayList<>();
            List<PrioDeviceInfo> prioDeviceInfos = new ArrayList<>();

            //first filter users by name
            for (User user : users) {
                String name = user.getName().toLowerCase();

                int foundIndex = name.indexOf(matchingString);

                if(foundIndex >= 0) {
                    prioUsers.add(new PrioUser(user, foundIndex));
                }
            }

            Collections.sort(prioUsers, (first, second) -> first.getPriority()-second.getPriority());

            filteredUsers = new ArrayList<>();

            for (PrioUser prioUser : prioUsers) {
                filteredUsers.add(prioUser.getUser());
            }

            //now filter devices by type, model and manufacturer
            for (DeviceInfo deviceInfo : deviceInfos) {
                HospitalDevice device = deviceInfo.getDevice();

                String type = device.getType().toLowerCase();
                String manufacturer = device.getManufacturer().toLowerCase();
                String model = device.getModel().toLowerCase();
                String serialNumber = device.getSerialNumber().toLowerCase();

                int typeIndex = type.indexOf(matchingString);
                int manufacturerIndex = manufacturer.indexOf(matchingString);
                int modelIndex = model.indexOf(matchingString);
                int serialNumberIndex = serialNumber.indexOf(matchingString);

                if(typeIndex >= 0 || manufacturerIndex >= 0 || modelIndex >= 0 || serialNumberIndex >= 0) {
                    if(typeIndex == -1) typeIndex = Integer.MAX_VALUE;
                    if(manufacturerIndex == -1) manufacturerIndex = Integer.MAX_VALUE;
                    if(modelIndex == -1) modelIndex = Integer.MAX_VALUE;
                    if(serialNumberIndex == -1) serialNumberIndex = Integer.MAX_VALUE;

                    int priority = Math.min(typeIndex, Math.min(manufacturerIndex, Math.min(modelIndex, serialNumberIndex)));

                    prioDeviceInfos.add(new PrioDeviceInfo(deviceInfo, priority));
                }
            }

            Collections.sort(prioDeviceInfos, (first, second) -> first.getPriority()-second.getPriority());

            filteredDeviceInfos = new ArrayList<>();

            for (PrioDeviceInfo prioDeviceInfo : prioDeviceInfos) {
                filteredDeviceInfos.add(prioDeviceInfo.getDeviceInfo());
            }

            this.notifyDataSetChanged();
        }

        /**
         * Wraps a user object and its filter priority.
         */
        private class PrioUser {
            private final User user;
            private final int priority;

            public PrioUser(User user, int priority) {
                this.user = user;
                this.priority = priority;
            }

            public User getUser() {
                return user;
            }

            public int getPriority() {
                return priority;
            }
        }

        /**
         * Wraps a deviceInfo object and its filter priority.
         */
        private class PrioDeviceInfo {
            private final DeviceInfo deviceInfo;
            private final int priority;

            public PrioDeviceInfo(DeviceInfo deviceInfo, int priority) {
                this.deviceInfo = deviceInfo;
                this.priority = priority;
            }

            public DeviceInfo getDeviceInfo() {
                return deviceInfo;
            }

            public int getPriority() {
                return priority;
            }
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch(i) {
                case ExpandableHospitalAdapter.DEVICES_GROUP:
                    return filteredDeviceInfos.size();
                case ExpandableHospitalAdapter.USERS_GROUP:
                    return filteredUsers.size();
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
                case ExpandableHospitalAdapter.DEVICES_GROUP:
                    return filteredDeviceInfos.get(childPosition);
                case ExpandableHospitalAdapter.USERS_GROUP:
                    return filteredUsers.get(childPosition);
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
                case ExpandableHospitalAdapter.USERS_GROUP:
                    nameView.setText(R.string.hospital_members_heading);
                    countView.setText(String.format(Locale.ROOT, "%d", filteredUsers.size()));
                    break;
                case ExpandableHospitalAdapter.DEVICES_GROUP:
                    nameView.setText(R.string.hospital_devices_heading);
                    countView.setText(String.format(Locale.ROOT, "%d", filteredDeviceInfos.size()));
                    break;
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            switch(groupPosition) {
                case ExpandableHospitalAdapter.USERS_GROUP:
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
                        ImageView phoneView = convertView.findViewById(R.id.phone_symbol);

                        nameView.setText(user.getName());
                        positionView.setText(user.getPosition());

                        if(user.getPhone() != null && user.getPhone().length() > 0) {
                            phoneView.setVisibility(View.VISIBLE);
                            phoneView.setOnClickListener((view) -> invokeCall(user.getPhone()));
                        }
                    }
                    break;
                case ExpandableHospitalAdapter.DEVICES_GROUP:
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
                        List<ReportInfo> reportInfos = deviceInfo.getReports();

                        statusView.setText(device.getLocation());

                        detailsView.setText(HospitalActivity.this.getString(R.string.line_break, device.getManufacturer(), device.getModel()));

                        nameView.setText(device.getType());

                        if(reportInfos.size() > 0) {
                            //reports have been sorted so latest report has index 0
                            Report lastReport = deviceInfo.getReports().get(0).getReport();

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
