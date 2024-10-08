package ngo.teog.swift.gui.deviceCreation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import org.apache.commons.text.WordUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.OrganizationalUnit;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Second step when creating a device: Enter device attributes.
 * @author nitelow
 */
public class NewDeviceActivity2 extends BaseActivity {

    private Button createButton;
    private ProgressBar progressBar;

    private int deviceNumber;

    private AutoCompleteTextView typeField, manufacturerField, modelField;
    private ArrayAdapter<String> typeAdapter, manufacturerAdapter, modelAdapter;

    private EditText assetNumberField, serialNumberField, intervalField;

    private Spinner departmentSpinner;

    public static final int MIN_MAINT_INTERVAL = 1;
    public static final int DEF_MAINT_INTERVAL = 3;
    public static final int MAX_MAINT_INTERVAL = 24;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device2);

        assetNumberField = findViewById(R.id.assetIdText);
        assetNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        typeField = findViewById(R.id.typeText);
        typeField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        typeField.setAdapter(typeAdapter);

        serialNumberField = findViewById(R.id.serialIdText);
        serialNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        manufacturerField = findViewById(R.id.manufacturerText);
        manufacturerField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        manufacturerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        manufacturerField.setAdapter(manufacturerAdapter);

        modelField = findViewById(R.id.modelText);
        modelField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        modelField.setAdapter(modelAdapter);

        departmentSpinner = findViewById(R.id.departmentSpinner);

        intervalField = findViewById(R.id.intervalField);
        intervalField.setText(Integer.toString(DEF_MAINT_INTERVAL));

        Button incButton = findViewById(R.id.incButton);
        incButton.setOnClickListener((view) -> {
            try {
                int oldInterval = Integer.parseInt(intervalField.getText().toString().trim());
                if(oldInterval+1 <= MAX_MAINT_INTERVAL) {
                    intervalField.setText(Integer.toString(oldInterval + 1));
                }
            } catch(NumberFormatException e) {
                intervalField.setText(Integer.toString(DEF_MAINT_INTERVAL));
            }
        });
        Button decButton = findViewById(R.id.decButton);
        decButton.setOnClickListener((view) -> {
            try {
                int oldInterval = Integer.parseInt(intervalField.getText().toString().trim());
                if(oldInterval-1 >= MIN_MAINT_INTERVAL) {
                    intervalField.setText(Integer.toString(oldInterval - 1));
                }
            } catch(NumberFormatException e) {
                intervalField.setText(Integer.toString(DEF_MAINT_INTERVAL));
            }
        });

        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener((view) -> createDevice());
        progressBar = findViewById(R.id.progressBar);

        Intent intent = this.getIntent();
        deviceNumber = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        NewDeviceViewModel2 viewModel = new ViewModelProvider(this, viewModelFactory).get(NewDeviceViewModel2.class);
        viewModel.init(id);

        viewModel.getOrgUnits().observe(this, organizationalUnits -> {
            if(organizationalUnits != null) {
                organizationalUnits.sort(Comparator.comparing(OrganizationalUnit::getName));
                organizationalUnits.add(0, null);

                departmentSpinner.setAdapter(new OrgUnitAdapter(this, organizationalUnits));
            }
        });

        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null) {
                Map<String, Integer> typeCountMap = new HashMap<>();
                Map<String, Integer> manufacturerCountMap = new HashMap<>();
                Map<String, Integer> modelCountMap = new HashMap<>();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    HospitalDevice device = deviceInfo.getDevice();

                    String type = WordUtils.capitalize(device.getType().trim());
                    String manufacturer = WordUtils.capitalize(device.getManufacturer().trim());
                    String model = device.getModel().trim();

                    updateSuggestionMap(typeCountMap, type);
                    updateSuggestionMap(manufacturerCountMap, manufacturer);
                    updateSuggestionMap(modelCountMap, model);
                }

                //Make sure each value is present at least three times, so spelling mistakes do not spread

                typeAdapter.clear();
                manufacturerAdapter.clear();
                modelAdapter.clear();

                for(Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
                    if(entry.getValue() >= 3) {
                        typeAdapter.add(entry.getKey());
                    }
                }

                for(Map.Entry<String, Integer> entry : manufacturerCountMap.entrySet()) {
                    if(entry.getValue() >= 3) {
                        manufacturerAdapter.add(entry.getKey());
                    }
                }

                for(Map.Entry<String, Integer> entry : modelCountMap.entrySet()) {
                    if(entry.getValue() >= 3) {
                        modelAdapter.add(entry.getKey());
                    }
                }
            }
        });
    }

    private void updateSuggestionMap(Map<String, Integer> map, String key) {
        if(map.containsKey(key)) {
            map.put(key, map.get(key)+1);
        } else {
            map.put(key, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_creation2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.newdevice_activity_2);
    }

    public void createDevice() {
        if(typeField.getText().length() > 0) {
            if(manufacturerField.getText().length() > 0) {
                if(modelField.getText().length() > 0) {
                    String assetNumber = assetNumberField.getText().toString().trim();

                    if(assetNumber.isEmpty()) {
                        assetNumber = Integer.toString(deviceNumber);
                    }

                    boolean intervalSet = false;
                    int interval = -1;

                    try {
                        interval = Integer.parseInt(intervalField.getText().toString().trim());
                        intervalSet = true;
                    } catch (NumberFormatException e) {
                        intervalField.setError("invalid number");
                    }

                    if(intervalSet) {
                        if(interval >= MIN_MAINT_INTERVAL && interval <= MAX_MAINT_INTERVAL) {
                            createButton.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);

                            OrganizationalUnit department = (OrganizationalUnit)departmentSpinner.getSelectedItem();
                            Integer departmentId = null;

                            if(department != null) {
                                departmentId = department.getId();
                            }

                            //we actually save the number of weeks, not months
                            HospitalDevice device = new HospitalDevice(
                                    deviceNumber,
                                    assetNumber,
                                    typeField.getText().toString().trim(),
                                    serialNumberField.getText().toString().trim(),
                                    manufacturerField.getText().toString().trim(),
                                    modelField.getText().toString().trim(),
                                    departmentId,
                                    -1,
                                    interval*4,
                                    true,
                                    new Date());

                            Intent intent = new Intent(NewDeviceActivity2.this, NewDeviceActivity3.class);
                            intent.putExtra(ResourceKeys.DEVICE, device);

                            startActivity(intent);
                            NewDeviceActivity2.this.finish();
                        } else {
                            intervalField.setError("interval must be between " + MIN_MAINT_INTERVAL + " and " + MAX_MAINT_INTERVAL + " months");
                        }
                    }
                } else {
                    modelField.setError(getString(R.string.empty_model));
                }
            } else {
                manufacturerField.setError(getString(R.string.empty_manufacturer));
            }
        } else {
            typeField.setError(getString(R.string.empty_type));
        }
    }

    private class OrgUnitAdapter extends ArrayAdapter<OrganizationalUnit> {
        public OrgUnitAdapter(Context context, List<OrganizationalUnit> orgUnits) {
            super(context, R.layout.spinner_default, orgUnits);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OrganizationalUnit orgUnit = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_default, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.text);

            if(orgUnit != null) {
                textView.setText(orgUnit.getName());
            } else {
                textView.setText(getContext().getString(R.string.none));
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            OrganizationalUnit orgUnit = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_default, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.text);

            if(departmentSpinner.getSelectedItemPosition() == position) {
                textView.setBackgroundColor(Color.LTGRAY);
            } else {
                textView.setBackgroundColor(Color.WHITE);
            }

            if(orgUnit != null) {
                textView.setText(orgUnit.getName());
            } else {
                textView.setText(getContext().getString(R.string.none));
            }

            return convertView;
        }
    }
}
