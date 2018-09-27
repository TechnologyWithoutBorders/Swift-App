package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.Triple;
import ngo.teog.swift.helpers.User;

public class HospitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        ArrayList<User> members = new ArrayList<>();
        ArrayList<HospitalDevice> devices = new ArrayList<>();

        ExpandableListView hospitalListView = findViewById(R.id.hospitalList);

        HospitalListAdapter hospitalListAdapter = new HospitalListAdapter(this, members, devices);
        hospitalListView.setAdapter(hospitalListAdapter);

        /*memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HospitalActivity.this, UserInfoActivity.class);
                intent.putExtra("user", (User)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });*/

        ProgressBar hospitalProgressBar = findViewById(R.id.hospitalProgressBar);

        /*deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });*/

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        if(this.checkForInternetConnection()) {
            /*SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int user = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            RequestFactory.ColleagueRequest request = new RequestFactory().createColleagueRequest(this, progressBar, memberListView, memberAdapter, user);
            RequestFactory.DeviceListRequest deviceRequest = new RequestFactory().createDeviceListRequest(this, deviceProgressBar, deviceListView, deviceAdapter, user);

            memberListView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            queue.add(request);

            deviceListView.setVisibility(View.INVISIBLE);
            deviceProgressBar.setVisibility(View.VISIBLE);

            queue.add(deviceRequest);*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_hospital, menu);
        return true;
    }

    private class HospitalListAdapter extends BaseExpandableListAdapter {
        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        private ArrayList<User> memberList;
        private ArrayList<HospitalDevice> deviceList;

        private final Context context;

        private HospitalListAdapter(Context context, ArrayList<User> memberList, ArrayList<HospitalDevice> deviceList) {
            this.context = context;
            this.memberList = memberList;
            this.deviceList = deviceList;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch(i) {
                case 0:
                    return memberList.size();
                case 1:
                    return deviceList.size();
                default:
                    return 0;
            }
        }

        @Override
        public Object getGroup(int i) {
            switch(i) {
                case 0:
                    return "Members";
                case 1:
                    return "Devices";
                default:
                    return null;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case 0:
                    return memberList.get(childPosition);
                case 1:
                    return deviceList.get(childPosition);
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
        public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.header_hospital, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView countView = convertView.findViewById(R.id.countView);

            switch(position) {
                case 0:
                    nameView.setText("Members");
                    countView.setText(Integer.toString(memberList.size()));
                    break;
                case 1:
                    nameView.setText("Devices");
                    countView.setText(Integer.toString(deviceList.size()));
                    break;
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            switch(groupPosition) {
                case 0:
                    if(convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.row_members, parent, false);
                    }

                    User user = memberList.get(childPosition);

                    if(user != null) {
                        TextView nameView = convertView.findViewById(R.id.nameView);
                        TextView positionView = convertView.findViewById(R.id.positionView);

                        nameView.setText(user.getName());
                        positionView.setText(user.getPosition());
                    }
                    break;
                case 1:
                    if(convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.row_maintenance, parent, false);
                    }

                    TextView nameView = convertView.findViewById(R.id.nameView);
                    TextView dateView = convertView.findViewById(R.id.dateView);
                    TextView statusView = convertView.findViewById(R.id.statusView);
                    ImageView imageView = convertView.findViewById(R.id.imageView);

                    HospitalDevice device = deviceList.get(childPosition);

                    if(device != null) {
                        nameView.setText(device.getType());

                        String dateString = DATE_FORMAT.format(device.getLastReportDate());
                        dateView.setText(dateString);

                        Triple triple = Triple.buildtriple(device.getState(), HospitalActivity.this);

                        statusView.setText(triple.getStatestring());

                        imageView.setImageDrawable(triple.getStateicon());
                        imageView.setBackgroundColor(triple.getBackgroundcolor());
                    }
                    break;
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    protected boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }
}
