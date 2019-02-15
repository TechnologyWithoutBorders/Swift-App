package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.User;

public class HospitalActivity extends BaseActivity {

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

        ProgressBar hospitalProgressBar = findViewById(R.id.hospitalProgressBar);

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        if(this.checkForInternetConnection()) {
            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int user = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            RequestFactory.DefaultRequest request = new RequestFactory().createHospitalRequest(this, hospitalProgressBar, contentView, nameView, locationView, hospitalListView, user);

            contentView.setVisibility(View.INVISIBLE);
            hospitalProgressBar.setVisibility(View.VISIBLE);

            queue.add(request);
        }
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
}
