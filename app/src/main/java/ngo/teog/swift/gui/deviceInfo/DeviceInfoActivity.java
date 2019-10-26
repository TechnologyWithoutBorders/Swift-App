package ngo.teog.swift.gui.deviceInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.ImageActivity;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity2;
import ngo.teog.swift.gui.reportCreation.ReportCreationActivity;
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Activity that sums up all available information about a device.
 */
public class DeviceInfoActivity extends BaseActivity {

    private static final int ASSET_NUMBER = 0;
    private static final int TYPE = 1;
    private static final int MODEL = 2;
    private static final int MANUFACTURER = 3;
    private static final int SERIAL_NUMBER = 4;
    private static final int WARD = 5;
    private static final int MAINTENANCE_INTERVAL = 6;

    private static final String[] PARAM_TITLES = {"Asset Number", "Type", "Model", "Manufacturer", "Serial Number", "Ward", "Maintenance Interval (Weeks)"};

    private ReportArrayAdapter adapter;

    private ListView reportListView;

    private Spinner statusSpinner;

    private ProgressBar progressBar;
    private TextView dummyImageView;
    private ImageView globalImageView;

    private TextView assetNumberView;
    private TextView typeView;
    private TextView modelView;
    private TextView manufacturerView;
    private TextView serialNumberView;
    private TextView wardView;
    private TextView intervalView;

    private DeviceInfo deviceInfo;

    @Inject
    ViewModelFactory viewModelFactory;

    private DeviceInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        //TODO für externe devices muss auch serializable möglich sein und dann ohne Bearbeitung usw.
        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        statusSpinner = findViewById(R.id.statusSpinner);
        statusSpinner.setAdapter(new StatusArrayAdapter(this, getResources().getStringArray(R.array.device_states)));

        reportListView = findViewById(R.id.reportList);

        reportListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Report report = ((ReportInfo)adapterView.getItemAtPosition(i)).getReport();

            Intent intent1 = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
            intent1.putExtra(ResourceKeys.DEVICE_ID, report.getDevice());
            intent1.putExtra(ResourceKeys.REPORT_ID, report.getId());
            startActivity(intent1);
        });

        dummyImageView = findViewById(R.id.downloadImageView);
        globalImageView = findViewById(R.id.imageView);

        //TODO bei Bild per Hash überprüfen, ob es ein neueres gibt

        progressBar = findViewById(R.id.progressBar);

        assetNumberView = findViewById(R.id.assetNumberView);
        typeView = findViewById(R.id.typeView);
        modelView = findViewById(R.id.modelView);
        manufacturerView = findViewById(R.id.manufacturerView);
        serialNumberView = findViewById(R.id.serialNumberView);

        TextView hospitalView = findViewById(R.id.hospitalView);

        wardView = findViewById(R.id.wardView);

        intervalView = findViewById(R.id.intervalView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DeviceInfoViewModel.class);
        viewModel.init(userId, deviceId);

        viewModel.getDeviceInfo().observe(this, deviceInfo -> {
            this.deviceInfo = deviceInfo;

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();

                List<ReportInfo> reports = deviceInfo.getReports();

                Collections.sort(reports, (first, second) -> second.getReport().getId()-first.getReport().getId());

                statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int currentState = deviceInfo.getReports().get(0).getReport().getCurrentState();

                        if(currentState != i) {
                            Intent intent = new Intent(DeviceInfoActivity.this, ReportCreationActivity.class);
                            intent.putExtra(ResourceKeys.REPORT_OLD_STATE, currentState);
                            intent.putExtra(ResourceKeys.REPORT_NEW_STATE, i);
                            intent.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                statusSpinner.setSelection(deviceInfo.getReports().get(0).getReport().getCurrentState());

                assetNumberView.setText(device.getAssetNumber());
                typeView.setText(device.getType());
                modelView.setText(device.getModel());
                manufacturerView.setText(device.getManufacturer());
                serialNumberView.setText(device.getSerialNumber());
                hospitalView.setText(deviceInfo.getHospitals().get(0).getName());
                wardView.setText(device.getWard());

                int interval = device.getMaintenanceInterval();

                if(interval % 4 == 0) {
                    intervalView.setText((interval/4) + " Months");
                } else {
                    intervalView.setText((interval) + " Weeks");
                }

                File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                dir.mkdirs();

                File image = new File(dir, device.getId() + ".jpg");

                if(!image.exists()) {
                    globalImageView.setVisibility(View.INVISIBLE);

                    dummyImageView.setOnClickListener(view -> downloadImage());
                } else {
                    dummyImageView.setVisibility(View.INVISIBLE);
                    globalImageView.setVisibility(View.VISIBLE);

                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    globalImageView.setImageBitmap(bitmap);
                    globalImageView.setBackgroundColor(Color.BLACK);

                    globalImageView.setOnClickListener(view -> {
                        File dir1 = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                        dir1.mkdirs();

                        File image1 = new File(dir1, device.getId() + ".jpg");

                        if(image1.exists()) {
                            Intent intent12 = new Intent(DeviceInfoActivity.this, ImageActivity.class);
                            intent12.putExtra(ResourceKeys.IMAGE, image1);

                            startActivity(intent12);
                        }
                    });
                }

                adapter = new ReportArrayAdapter(this, reports);
                reportListView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    private void refresh() {
        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel.refreshHospital(userId);
    }

    public void editAssetNumber(View view) {
        this.edit(ASSET_NUMBER, assetNumberView.getText().toString(), 25);
    }

    public void editType(View view) {
        this.edit(TYPE, typeView.getText().toString(), 25);
    }

    public void editModel(View view) {
        this.edit(MODEL, modelView.getText().toString(), 25);
    }

    public void editManufacturer(View view) {
        this.edit(MANUFACTURER, manufacturerView.getText().toString(), 25);
    }

    public void editSerialNumber(View view) {
        this.edit(SERIAL_NUMBER, serialNumberView.getText().toString(), 25);
    }

    public void editWard(View view) {
        this.edit(WARD, wardView.getText().toString(), 25);
    }

    public void editMaintenanceInterval(View view) {
        this.edit(MAINTENANCE_INTERVAL, null, 0);
    }

    private void edit(int parameter, String presetText, int maxLength) {
        if(deviceInfo != null) {
            String titleString = PARAM_TITLES[parameter];

            final View editView;

            switch(parameter) {
                case MAINTENANCE_INTERVAL:
                    NumberPicker numberPicker = new NumberPicker(this);
                    numberPicker.setMinValue(NewDeviceActivity2.MIN_MAINT_INTERVAL);
                    numberPicker.setMaxValue(NewDeviceActivity2.MAX_MAINT_INTERVAL);
                    numberPicker.setValue(deviceInfo.getDevice().getMaintenanceInterval());

                    editView = numberPicker;

                    break;
                default:
                    EditText editText = new EditText(this);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    editText.setText(presetText);

                    editView = editText;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(titleString);
            builder.setView(editView);

            DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> save(parameter, editView);
            DialogInterface.OnClickListener negativeListener = (dialogInterface, i) -> dialogInterface.cancel();

            builder.setPositiveButton(getText(R.string.dialog_ok_text), positiveListener);
            builder.setNegativeButton(getText(R.string.dialog_cancel_text), negativeListener);

            builder.show();
        }
    }

    private void save(int parameter, View editView) {
        if(deviceInfo != null) {
            HospitalDevice device = deviceInfo.getDevice();

            switch (parameter) {
                case ASSET_NUMBER:
                    String assetNumber = ((EditText) editView).getText().toString();

                    //if we use database queries this value is updated automatically
                    assetNumberView.setText(assetNumber);
                    device.setAssetNumber(assetNumber);
                    break;
                case TYPE:
                    String type = ((EditText) editView).getText().toString();

                    typeView.setText(type);
                    device.setType(type);
                    break;
                case MODEL:
                    String model = ((EditText) editView).getText().toString();

                    modelView.setText(model);
                    device.setModel(model);
                    break;
                case MANUFACTURER:
                    String manufacturer = ((EditText) editView).getText().toString();

                    manufacturerView.setText(manufacturer);
                    device.setManufacturer(manufacturer);
                    break;
                case SERIAL_NUMBER:
                    String serialNumber = ((EditText) editView).getText().toString();

                    serialNumberView.setText(serialNumber);
                    device.setSerialNumber(serialNumber);
                    break;
                case WARD:
                    String ward = ((EditText) editView).getText().toString();

                    wardView.setText(ward);
                    device.setWard(ward);
                    break;
                case MAINTENANCE_INTERVAL:
                    int interval = ((NumberPicker) editView).getValue();

                    device.setMaintenanceInterval(interval);

                    if (interval % 4 == 0) {
                        intervalView.setText(interval / 4 + " Months");
                    } else {
                        intervalView.setText(interval + " Weeks");
                    }

                    break;
            }

            device.setLastUpdate(new Date());

            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            viewModel.updateDevice(device, userId);
        }
    }

    private void downloadImage() {
        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.DefaultRequest request = RequestFactory.getInstance().createDeviceImageRequest(this, progressBar, globalImageView, deviceInfo.getDevice().getId());

            progressBar.setVisibility(View.VISIBLE);
            dummyImageView.setVisibility(View.GONE);

            queue.add(request);
        } else {
            Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this device: http://teog.virlep.de/device/" + deviceInfo.getHospitals().get(0).getId() + "/" + deviceInfo.getDevice().getId());
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share device link"));
                return true;
            default:
                return super.onOptionsItemSelected(item, R.string.deviceinfo_activity);
        }
    }

    /**
     * Adapter for displaying the recent report list.
     */
    private class ReportArrayAdapter extends ArrayAdapter<ReportInfo> {
        private final Context context;
        private DateFormat dateFormat = new SimpleDateFormat(Defaults.DATE_PATTERN);

        private ReportArrayAdapter(Context context, List<ReportInfo> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_reports, parent, false);
            }

            ReportInfo reportInfo = this.getItem(position);

            if(reportInfo != null) {
                Report report = reportInfo.getReport();

                TextView authorView = convertView.findViewById(R.id.authorView);
                TextView dateView = convertView.findViewById(R.id.dateView);
                ImageView fromState = convertView.findViewById(R.id.fromState);

                DeviceState triple = DeviceState.buildState(report.getPreviousState(),this.getContext());

                fromState.setImageDrawable(triple.getStateicon());
                fromState.setColorFilter(triple.getBackgroundcolor());

                ImageView toState = convertView.findViewById(R.id.toState);

                DeviceState triple1 = DeviceState.buildState(report.getCurrentState(),this.getContext());

                toState.setImageDrawable(triple1.getStateicon());
                toState.setColorFilter(triple1.getBackgroundcolor());

                authorView.setText(reportInfo.getAuthors().get(0).getName());

                Date date = report.getCreated();
                dateView.setText(dateFormat.format(date));
            }

            return convertView;
        }
    }

    /**
     * Adapter for displaying the device state spinner.
     */
    private class StatusArrayAdapter extends ArrayAdapter<String> {

        private final Context context;

        private StatusArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_status, parent, false);
            }

            TextView statusTextView = convertView.findViewById(R.id.statusTextView);
            statusTextView.setText(getItem(position));

            ImageView statusImageView = convertView.findViewById(R.id.statusImageView);

            DeviceState triple = DeviceState.buildState(position,this.getContext());

            statusImageView.setImageDrawable(triple.getStateicon());
            statusImageView.setBackgroundColor(triple.getBackgroundcolor());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }
}
