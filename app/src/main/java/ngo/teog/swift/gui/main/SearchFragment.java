package ngo.teog.swift.gui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;

import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.User;

public class SearchFragment extends BaseFragment {

    private static final int DEVICE = 0;
    private static final int USER = 1;

    private ProgressBar progressBar;
    private EditText searchField;
    private Spinner searchSpinner;
    private Button searchButton;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchSpinner = rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.search_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = view.findViewById(R.id.maintenanceList);

        progressBar = view.findViewById(R.id.progressBar);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchObject item = (SearchObject)adapterView.getItemAtPosition(i);

                Intent intent = new Intent(getContext(), item.getInfoActivityClass());
                intent.putExtra(item.getExtraIdentifier(), item);
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
            if(this.checkForInternetConnection()) {
                String searchString = searchField.getText().toString();
                RequestQueue queue = VolleyManager.getInstance(getContext()).getRequestQueue();

                progressBar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.INVISIBLE);

                JsonObjectRequest request = null;

                switch(searchSpinner.getSelectedItemPosition()) {
                    case DEVICE:
                        DeviceArrayAdapter deviceAdapter = new DeviceArrayAdapter(getContext(), new ArrayList<HospitalDevice>());
                        listView.setAdapter(deviceAdapter);

                        request = new RequestFactory().createDeviceSearchRequest(getContext(), progressBar, searchButton, searchString, deviceAdapter);

                        break;

                    case USER:
                        UserArrayAdapter userAdapter = new UserArrayAdapter(getContext(), new ArrayList<User>());
                        listView.setAdapter(userAdapter);

                        request = new RequestFactory().createUserSearchRequest(getContext(), progressBar, searchButton, searchString, userAdapter);

                        break;
                }

                queue.add(request);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            } else {
                Toast.makeText(this.getContext(), "no internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            searchField.setError("invalid search value");
        }
    }

    private class DeviceArrayAdapter extends ArrayAdapter<HospitalDevice> {

        public DeviceArrayAdapter(Context context, ArrayList<HospitalDevice> values) {
            super(context, -1, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater)this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_search, parent, false);
            }

            TextView primaryInfo = convertView.findViewById(R.id.primaryInfo);
            TextView secondaryInfo = convertView.findViewById(R.id.secondaryInfo);
            TextView category = convertView.findViewById(R.id.category);

            HospitalDevice device = this.getItem(position);

            primaryInfo.setText(device.getManufacturer());
            secondaryInfo.setText(device.getModel());
            category.setText(device.getType());

            return convertView;
        }
    }

    private class UserArrayAdapter extends ArrayAdapter<User> {

        public UserArrayAdapter(Context context, ArrayList<User> values) {
            super(context, -1, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater)this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_search, parent, false);
            }

            TextView primaryInfo = convertView.findViewById(R.id.primaryInfo);
            TextView secondaryInfo = convertView.findViewById(R.id.secondaryInfo);
            TextView category = convertView.findViewById(R.id.category);

            User user = this.getItem(position);

            primaryInfo.setText(user.getName());
            secondaryInfo.setText(user.getPosition());
            //category.setText(user.getHospital());

            return convertView;
        }
    }
}
