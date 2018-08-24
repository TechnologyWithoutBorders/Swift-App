package ngo.teog.swift.gui.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
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
import ngo.teog.swift.helpers.AlarmReceiver;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.Triple;

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

        BroadcastReceiver receiver = new WorkFetchedBroadcastReceiver();
        IntentFilter filter = new IntentFilter("ngo.swift.teog.WORK_FETCHED");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);

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

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    private void refresh() {
        if(this.checkForInternetConnection()) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        }
    }

    public class WorkFetchedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(this.checkForInternetConnection(context)) {
                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

                RequestFactory.DeviceListRequest request = new RequestFactory().createTodoListRequest(context, progressBar, listView, adapter);

                queue.add(request);
            }
        }

        private boolean checkForInternetConnection(Context context) {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if(cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
            } else {
                return false;
            }
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

                String dateString = DATE_FORMAT.format(device.getLastReportDate());
                dateView.setText(dateString);

                Triple triple = Triple.buildtriple(device.getState(),this.getContext());

                statusView.setText(triple.getStatestring());
                imageView.setImageDrawable(triple.getStateicon());
                imageView.setBackgroundColor(triple.getBackgroundcolor());
            }

            return convertView;
        }
    }
}
