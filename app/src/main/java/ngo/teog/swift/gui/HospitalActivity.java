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

        ArrayList<User> values = new ArrayList<>();

        ProgressBar progressBar = findViewById(R.id.memberProgressBar);
        ListView memberListView = findViewById(R.id.memberList);

        MemberListAdapter memberAdapter = new MemberListAdapter(this, values);
        memberListView.setAdapter(memberAdapter);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HospitalActivity.this, UserInfoActivity.class);
                intent.putExtra("user", (User)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        ArrayList<SearchObject> devices = new ArrayList<>();

        ProgressBar deviceProgressBar = findViewById(R.id.deviceProgressBar);
        ListView deviceListView = findViewById(R.id.deviceList);

        DeviceListAdapter deviceAdapter = new DeviceListAdapter(this, devices);
        deviceListView.setAdapter(deviceAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HospitalActivity.this, DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        if(this.checkForInternetConnection()) {
            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int user = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            RequestFactory.ColleagueRequest request = new RequestFactory().createColleagueRequest(this, progressBar, memberListView, memberAdapter, user);
            RequestFactory.DeviceListRequest deviceRequest = new RequestFactory().createDeviceListRequest(this, deviceProgressBar, deviceListView, deviceAdapter, user);

            memberListView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            queue.add(request);

            deviceListView.setVisibility(View.INVISIBLE);
            deviceProgressBar.setVisibility(View.VISIBLE);

            queue.add(deviceRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_hospital, menu);
        return true;
    }

    private class MemberListAdapter extends ArrayAdapter<User> {
        private final Context context;

        private MemberListAdapter(Context context, ArrayList<User> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_members, parent, false);
            }

            User user = this.getItem(position);

            if(user != null) {
                TextView nameView = convertView.findViewById(R.id.nameView);
                TextView positionView = convertView.findViewById(R.id.positionView);

                nameView.setText(user.getName());
                positionView.setText(user.getPosition());
            }

            return convertView;
        }
    }

    private class DeviceListAdapter extends ArrayAdapter<SearchObject> {
        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private final Context context;

        private DeviceListAdapter(Context context, ArrayList<SearchObject> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_maintenance, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            TextView statusView = convertView.findViewById(R.id.statusView);
            ImageView imageView = convertView.findViewById(R.id.imageView);

            HospitalDevice device = (HospitalDevice)this.getItem(position);

            if(device != null) {
                nameView.setText(device.getType());

                String dateString = DATE_FORMAT.format(device.getLastReportDate());
                dateView.setText(dateString);

                Triple triple = Triple.buildtriple(device.getState(), this.getContext());

                statusView.setText(triple.getStatestring());

                imageView.setImageDrawable(triple.getStateicon());
                imageView.setBackgroundColor(triple.getBackgroundcolor());
            }

            return convertView;
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
