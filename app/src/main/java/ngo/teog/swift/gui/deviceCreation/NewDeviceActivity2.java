package ngo.teog.swift.gui.deviceCreation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProviders;

import org.apache.commons.text.WordUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.hospital.HospitalViewModel;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class NewDeviceActivity2 extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private int deviceNumber;

    private EditText assetNumberField;

    private AutoCompleteTextView typeField;
    private ArrayAdapter<String> typeAdapter;

    private EditText serialNumberField;

    private AutoCompleteTextView manufacturerField;
    private ArrayAdapter<String> manufacturerAdapter;

    private AutoCompleteTextView modelField;
    private ArrayAdapter<String> modelAdapter;

    private AutoCompleteTextView wardField;
    private ArrayAdapter<String> wardAdapter;

    private NumberPicker intervalPicker;
    private Spinner weekMonthSpinner;

    public static final int MIN_MAINT_INTERVAL = 1;
    public static final int DEF_MAINT_INTERVAL = 4;
    public static final int MAX_MAINT_INTERVAL = 24;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device2);

        assetNumberField = findViewById(R.id.assetNumberText);
        assetNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        typeField = findViewById(R.id.typeText);
        typeField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        typeField.setAdapter(typeAdapter);

        serialNumberField = findViewById(R.id.serialNumberText);
        serialNumberField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        manufacturerField = findViewById(R.id.manufacturerText);
        manufacturerField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        manufacturerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        manufacturerField.setAdapter(manufacturerAdapter);

        modelField = findViewById(R.id.modelText);
        modelField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        modelField.setAdapter(modelAdapter);

        wardField = findViewById(R.id.wardText);
        wardField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        wardField.setAdapter(wardAdapter);

        intervalPicker = findViewById(R.id.intervalPicker);
        weekMonthSpinner = findViewById(R.id.spinner2);

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        NumberPicker intervalPicker = findViewById(R.id.intervalPicker);
        intervalPicker.setMinValue(MIN_MAINT_INTERVAL);
        intervalPicker.setMaxValue(MAX_MAINT_INTERVAL);
        intervalPicker.setValue(DEF_MAINT_INTERVAL);

        Intent intent = this.getIntent();
        deviceNumber = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        HospitalViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(HospitalViewModel.class);
        viewModel.init(id);

        viewModel.getDeviceInfos().observe(this, deviceInfos -> {
            if(deviceInfos != null) {
                Map<String, Integer> typeCountMap = new HashMap<>();
                Map<String, Integer> manufacturerCountMap = new HashMap<>();
                Map<String, Integer> modelCountMap = new HashMap<>();
                Map<String, Integer> wardCountMap = new HashMap<>();

                for(DeviceInfo deviceInfo : deviceInfos) {
                    HospitalDevice device = deviceInfo.getDevice();

                    String type = WordUtils.capitalize(device.getType());
                    String manufacturer = WordUtils.capitalize(device.getManufacturer());
                    String model = device.getModel();
                    String ward = WordUtils.capitalize(device.getWard());

                    updateSuggestionMap(typeCountMap, type);
                    updateSuggestionMap(manufacturerCountMap, manufacturer);
                    updateSuggestionMap(modelCountMap, model);
                    updateSuggestionMap(wardCountMap, ward);
                }

                //Make sure each value is present at least three times, so spelling mistakes do not spread

                typeAdapter.clear();
                manufacturerAdapter.clear();
                modelAdapter.clear();
                wardAdapter.clear();

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

                for(Map.Entry<String, Integer> entry : wardCountMap.entrySet()) {
                    if(entry.getValue() >= 3) {
                        wardAdapter.add(entry.getKey());
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

    public void createDevice(View view) {
        if(typeField.getText().length() > 0) {
            if(manufacturerField.getText().length() > 0) {
                if(modelField.getText().length() > 0) {
                    nextButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    String assetNumber = assetNumberField.getText().toString();

                    if(assetNumber.length() == 0) {
                        assetNumber = Integer.toString(deviceNumber);
                    }

                    int interval;

                    if((weekMonthSpinner.getSelectedItem()).equals("Week")) {
                        interval = intervalPicker.getValue();
                    } else {
                        interval = intervalPicker.getValue()*4;
                    }

                    HospitalDevice device = new HospitalDevice(deviceNumber, assetNumber,
                            typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), wardField.getText().toString(), -1, interval, new Date());

                    Intent intent = new Intent(NewDeviceActivity2.this, NewDeviceActivity3.class);
                    intent.putExtra(ResourceKeys.DEVICE, device);

                    startActivity(intent);
                } else {
                    modelField.setError("empty model");
                }
            } else {
                manufacturerField.setError("empty manufacturer");
            }
        } else {
            typeField.setError("empty type");
        }
    }
}
