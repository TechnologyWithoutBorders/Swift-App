package ngo.teog.hstest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import ngo.teog.hstest.comm.RequestFactory;
import ngo.teog.hstest.comm.VolleyManager;
import ngo.teog.hstest.helpers.Defaults;
import ngo.teog.hstest.helpers.HospitalDevice;

public class MainActivity extends BaseActivity {

    private MySimpleArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        listView = (ListView)findViewById(R.id.maintenanceList);
        ArrayList<HospitalDevice> values = new ArrayList<HospitalDevice>();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new MySimpleArrayAdapter(this, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceRequest(this, progressBar, listView, null, adapter);

        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);
    }

    @Override
    public void onInternetStatusChanged() {
        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceRequest(this, progressBar, listView, null, adapter);

        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aboutItem:
                startAboutActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void startQRActivity(View view) {
        Intent intent = new Intent(MainActivity.this, QRActivity.class);
        startActivity(intent);
    }

    public void startUserProfileActivity(View view) {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void startTodoActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TodoActivity.class);
        startActivity(intent);
    }

    public void startSearchActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void startNewDeviceActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NewDeviceActivity.class);
        startActivity(intent);
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<HospitalDevice> {
        private final Context context;

        public MySimpleArrayAdapter(Context context, ArrayList<HospitalDevice> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_maintenance, parent, false);

            TextView nameView = rowView.findViewById(R.id.nameView);
            TextView dateView = rowView.findViewById(R.id.dateView);

            if(this.getItem(position) != null) {
                nameView.setText(this.getItem(position).getAssetNumber());
            } else {
                nameView.setText("no internet connection");
                nameView.setTextColor(Color.RED);
                dateView.setText(null);
            }

            return rowView;
        }
    }
}
