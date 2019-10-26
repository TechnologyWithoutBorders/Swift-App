package ngo.teog.swift.gui.maintenance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class SearchActivity3 extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private SearchViewModel viewModel;

    private ProgressBar progressBar;
    private EditText searchField;
    private Button searchButton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        Intent intent = getIntent();

        String searchObject = intent.getStringExtra(Defaults.SEARCH_OBJECT);
        String scope = intent.getStringExtra(Defaults.SCOPE);

        progressBar = findViewById(R.id.progressBar);
        searchField = findViewById(R.id.searchField);
        searchButton = findViewById(R.id.search_button);
        listView = findViewById(R.id.maintenanceList);

        searchField.setHint(searchObject);

        if(Defaults.SCOPE_GLOBAL.equals(scope)) {
            searchButton.setOnClickListener(view -> searchOnline(searchObject));
        } else if(Defaults.SCOPE_LOCAL.equals(scope)) {
            DaggerAppComponent.builder()
                    .appModule(new AppModule(getApplication()))
                    .roomModule(new RoomModule(getApplication()))
                    .build()
                    .inject(this);

            searchButton.setOnClickListener(view -> searchOffline(searchObject));
        }
    }

    private void searchOnline(String searchObject) {
        if(searchField.getText().length() > 0) {
            if(this.checkForInternetConnection()) {
                String searchString = searchField.getText().toString();
                RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

                progressBar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.INVISIBLE);

                JsonObjectRequest request = null;

                if(ResourceKeys.DEVICE.equals(searchObject)) {
                    DeviceArrayAdapter deviceAdapter = new DeviceArrayAdapter(this, new ArrayList<>());
                    listView.setAdapter(deviceAdapter);

                    request = RequestFactory.getInstance().createDeviceSearchRequest(this, progressBar, searchButton, searchString, deviceAdapter);
                } else if(ResourceKeys.USER.equals(searchObject)) {
                    UserArrayAdapter userAdapter = new UserArrayAdapter(this, new ArrayList<>());
                    listView.setAdapter(userAdapter);

                    listView.setOnItemClickListener((adapterView, view, i, l) -> {
                        User item = (User)adapterView.getItemAtPosition(i);

                        Intent intent = new Intent(SearchActivity3.this, UserInfoActivity.class);
                        intent.putExtra(ResourceKeys.USER, item);
                        startActivity(intent);
                    });

                    request = RequestFactory.getInstance().createUserSearchRequest(this, progressBar, searchButton, searchString, userAdapter);
                } if(ResourceKeys.HOSPITAL.equals(searchObject)) {
                    //TODO
                }

                queue.add(request);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            } else {
                Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            searchField.setError("invalid search value");
        }
    }

    private void searchOffline(String searchObject) {
        if(searchField.getText().length() > 0) {

        } else {
            searchField.setError("invalid search value");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintenance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.maintenance_info);
    }

    private class DeviceArrayAdapter extends ArrayAdapter<HospitalDevice> {

        public DeviceArrayAdapter(Context context, ArrayList<HospitalDevice> values) {
            super(context, -1, values);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
