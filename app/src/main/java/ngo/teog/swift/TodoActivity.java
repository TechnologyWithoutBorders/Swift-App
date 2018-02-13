package ngo.teog.swift;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ngo.teog.swift.comm.RequestFactory;
import ngo.teog.swift.comm.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;

public class TodoActivity extends Fragment {

    private MySimpleArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    //TODO Die Konstante muss natürlich hier weg
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_todo, container, false);

        listView = rootView.findViewById(R.id.maintenanceList);
        ArrayList<HospitalDevice> values = new ArrayList<HospitalDevice>();

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        adapter = new MySimpleArrayAdapter(getContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

        RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceRequest(getContext(), progressBar, listView, null, adapter);

        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        return rootView;
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

            HospitalDevice device = this.getItem(position);

            if(device != null) {
                nameView.setText(device.getType());

                String dateString = DATE_FORMAT.format(device.getNextMaintenance());
                dateView.setText(dateString);
            } else {
                nameView.setText("no internet connection");
                nameView.setTextColor(Color.RED);
                dateView.setText(null);
            }

            return rowView;
        }
    }
}
