package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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

import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.gui.DeviceInfoActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.SearchObject;

public class TodoFragment extends BaseFragment {

    private MySimpleArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    //TODO Die Konstante muss natürlich hier weg
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = view.findViewById(R.id.maintenanceList);
        ArrayList<SearchObject> values = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);

        adapter = new MySimpleArrayAdapter(getContext(), values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
                intent.putExtra("device", (HospitalDevice)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                    refresh();
                }
            }
        );

        refresh();
    }

    private void refresh() {
        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

            RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceRequest(getContext(), progressBar, listView, null, adapter);

            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            queue.add(request);
        }
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<SearchObject> {
        private final Context context;

        private MySimpleArrayAdapter(Context context, ArrayList<SearchObject> values) {
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

                String dateString = DATE_FORMAT.format(device.getNextMaintenance());
                dateView.setText(dateString);

                if(device.isWorking()) {
                    statusView.setText("maintenance");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_maintenance));
                    imageView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    statusView.setText("repair");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repair));
                    imageView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            }

            return convertView;
        }
    }
}
