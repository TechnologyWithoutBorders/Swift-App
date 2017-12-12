package ngo.teog.hstest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ngo.teog.hstest.comm.DeviceListFetchTask;
import ngo.teog.hstest.helpers.HospitalDevice;

public class TodoActivity extends BaseActivity {

    private MySimpleArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        ArrayList<HospitalDevice> values = new ArrayList<HospitalDevice>();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        listView = (ListView)findViewById(R.id.maintenanceList);

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

        new DeviceListFetchTask(this, listView, progressBar, adapter).execute(null, null);
    }

    @Override
    public void onInternetStatusChanged() {
        new DeviceListFetchTask(this, listView, progressBar, adapter).execute(null, null);
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
                nameView.setText(this.getItem(position).getName());

                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

                Date due = this.getItem(position).getDue();

                if(due.before(new Date())) {
                    dateView.setTextColor(Color.RED);
                }
                dateView.setText(format.format(this.getItem(position).getDue()));
            } else {
                nameView.setText("no internet connection");
                nameView.setTextColor(Color.RED);
                dateView.setText(null);
            }

            return rowView;
        }
    }
}
