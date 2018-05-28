package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.gui.DeviceInfoActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.Filter;
import ngo.teog.swift.helpers.HospitalDevice;

public class SearchFragment extends BaseFragment {

    private MySimpleArrayAdapter adapter;
    private ProgressBar progressBar;
    private EditText searchField;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_search, container, false);

        Spinner spinner = rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.search_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.maintenanceList);
        ArrayList<HospitalDevice> values = new ArrayList<>();

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

        searchButton = view.findViewById(R.id.button22);
        searchField = view.findViewById(R.id.editText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search() {
        if(searchField.getText().length() > 0) {
            String searchString = searchField.getText().toString();

            Filter[] filters = {new Filter(DeviceFilter.TYPE, searchString)};

            if(this.checkForInternetConnection()) {
                RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

                RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceSearchRequest(getContext(), progressBar, searchButton, filters, adapter);

                progressBar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.INVISIBLE);

                queue.add(request);
            }
        } else {
            searchField.setError("invalid search value");
        }
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<HospitalDevice> {
        private final Context context;

        public MySimpleArrayAdapter(Context context, ArrayList<HospitalDevice> values) {
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

            HospitalDevice device = this.getItem(position);

            if(device != null) {
                nameView.setText(device.getType());

                String dateString = Defaults.DATE_FORMAT.format(device.getNextMaintenance());
                dateView.setText(dateString);
            } else {
                nameView.setText("no internet connection");
                nameView.setTextColor(Color.RED);
                dateView.setText(null);
            }

            return convertView;
        }
    }
}
