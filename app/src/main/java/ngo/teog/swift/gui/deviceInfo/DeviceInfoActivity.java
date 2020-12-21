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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.ImageActivity;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity2;
import ngo.teog.swift.gui.reportCreation.ReportCreationActivity;
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
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

    private Button reportCreationButton;

    private ListView reportListView;

    private ProgressBar progressBar;
    private TextView dummyImageView;
    private ImageView globalImageView;

    private ProgressBar documentProgressBar;
    private ImageView documentButton;
    private LinearLayout stateSection;
    private TableLayout attributeTable;

    private TextView assetNumberView, typeView, modelView, manufacturerView, serialNumberView, wardView, intervalView;

    private DeviceInfo deviceInfo;

    @Inject
    ViewModelFactory viewModelFactory;

    private DeviceInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();

        int deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        reportCreationButton = findViewById(R.id.reportCreationButton);

        ImageView stateImageView = findViewById(R.id.stateView);
        TextView stateTextView = findViewById(R.id.stateTextView);

        documentProgressBar = findViewById(R.id.documentProgressBar);
        documentButton = findViewById(R.id.documentButton);
        stateSection = findViewById(R.id.stateSection);
        attributeTable = findViewById(R.id.attributeTable);

        reportListView = findViewById(R.id.reportList);

        reportListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Report report = ((ReportInfo)adapterView.getItemAtPosition(i)).getReport();

            Intent intent1 = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
            intent1.putExtra(ResourceKeys.DEVICE_ID, report.getDevice());
            intent1.putExtra(ResourceKeys.REPORT_ID, report.getId());
            intent1.putExtra(ResourceKeys.HOSPITAL_ID, report.getHospital());
            startActivity(intent1);
        });

        dummyImageView = findViewById(R.id.downloadImageView);
        globalImageView = findViewById(R.id.imageView);
        globalImageView.setBackgroundColor(Color.BLACK);

        globalImageView.setOnClickListener(view -> {
            File dir = new File(this.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
            dir.mkdirs();

            File image1 = new File(dir, deviceId + ".jpg");

            if(image1.exists()) {
                Intent intent12 = new Intent(DeviceInfoActivity.this, ImageActivity.class);
                intent12.putExtra(ResourceKeys.IMAGE, image1);
                intent12.putExtra(ResourceKeys.DEVICE_ID, deviceId);

                startActivity(intent12);
            }
        });

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

        viewModel = new ViewModelProvider(this, viewModelFactory).get(DeviceInfoViewModel.class);
        viewModel.init(userId, deviceId);

        viewModel.getDeviceInfo().observe(this, deviceInfo -> {
            this.deviceInfo = deviceInfo;

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();

                List<ReportInfo> reports = deviceInfo.getReports();

                Collections.sort(reports, (first, second) -> second.getReport().getId()-first.getReport().getId());

                reportCreationButton.setOnClickListener((view) -> {
                    int currentState = deviceInfo.getReports().get(0).getReport().getCurrentState();

                    Intent reportIntent = new Intent(DeviceInfoActivity.this, ReportCreationActivity.class);
                    reportIntent.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                    reportIntent.putExtra(ResourceKeys.REPORT_OLD_STATE, currentState);
                    startActivity(reportIntent);
                });

                DeviceStateVisuals visuals = new DeviceStateVisuals(deviceInfo.getReports().get(0).getReport().getCurrentState(), this);

                stateTextView.setText(visuals.getStateString());
                stateImageView.setImageDrawable(visuals.getStateIcon());
                stateImageView.setBackgroundColor(visuals.getBackgroundColor());

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

    private void refresh() {//TODO Reports werden nicht refresht! Bild irgendwie schon, auch wenn hier nicht angegeben
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
                    String assetNumber = ((EditText) editView).getText().toString().trim();

                    //if we use database queries this value is updated automatically
                    assetNumberView.setText(assetNumber);
                    device.setAssetNumber(assetNumber);
                    break;
                case TYPE:
                    String type = ((EditText) editView).getText().toString().trim();

                    typeView.setText(type);
                    device.setType(type);
                    break;
                case MODEL:
                    String model = ((EditText) editView).getText().toString().trim();

                    modelView.setText(model);
                    device.setModel(model);
                    break;
                case MANUFACTURER:
                    String manufacturer = ((EditText) editView).getText().toString().trim();

                    manufacturerView.setText(manufacturer);
                    device.setManufacturer(manufacturer);
                    break;
                case SERIAL_NUMBER:
                    String serialNumber = ((EditText) editView).getText().toString().trim();

                    serialNumberView.setText(serialNumber);
                    device.setSerialNumber(serialNumber);
                    break;
                case WARD:
                    String ward = ((EditText) editView).getText().toString().trim();

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

            JsonObjectRequest request = RequestFactory.getInstance().createDeviceImageRequest(this, progressBar, globalImageView, deviceInfo.getDevice().getId());

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
                SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this device: http://teog.virlep.de/device/" + preferences.getString(Defaults.COUNTRY_PREFERENCE, null) + "/" + deviceInfo.getHospitals().get(0).getId() + "/" + deviceInfo.getDevice().getId());
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share device link"));
                return true;
            case R.id.info:
                //Show tutorial
                FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                        .add(buildTutorialStep(attributeTable, "This table lists all relevant data of a device."))
                        .add(buildTutorialStep(stateSection, "The current state of the device is shown up here."))
                        .add(buildTutorialStep(reportListView, "You can see the recent maintenance/repair history in the bottom section."))
                        .add(buildTutorialStep(reportCreationButton, "Use this button to create a new report."))
                        .add(buildTutorialStep(documentButton, "If available, related documents can be retrieved by tapping this button."));

                tutorialQueue.show();

                return true;
            default:
                return super.onOptionsItemSelected(item, R.string.deviceinfo_activity);
        }
    }

    public void searchDocuments(View view) {
        if(this.checkForInternetConnection()) {
            documentButton.setVisibility(View.INVISIBLE);
            documentProgressBar.setVisibility(View.VISIBLE);

            JsonObjectRequest request = RequestFactory.getInstance().createDeviceDocumentRequest(this, deviceInfo.getDevice(), documentButton, documentProgressBar);

            VolleyManager.getInstance(this).getRequestQueue().add(request);
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter for displaying the recent report list.
     */
    private class ReportArrayAdapter extends ArrayAdapter<ReportInfo> {
        private final Context context;
        private DateFormat dateFormat = new SimpleDateFormat(Defaults.DATE_PATTERN, Locale.getDefault());

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

                TextView titleView = convertView.findViewById(R.id.title_view);
                TextView dateView = convertView.findViewById(R.id.dateView);
                ImageView fromState = convertView.findViewById(R.id.fromState);

                DeviceStateVisuals triple = new DeviceStateVisuals(report.getPreviousState(),this.getContext());

                fromState.setImageDrawable(triple.getStateIcon());
                fromState.setColorFilter(triple.getBackgroundColor());

                ImageView toState = convertView.findViewById(R.id.toState);

                DeviceStateVisuals triple1 = new DeviceStateVisuals(report.getCurrentState(),this.getContext());

                toState.setImageDrawable(triple1.getStateIcon());
                toState.setColorFilter(triple1.getBackgroundColor());

                String title = reportInfo.getReport().getTitle();

                if(title.length() > 0) {
                    titleView.setText(title);
                } else {
                    titleView.setText(reportInfo.getAuthors().get(0).getName());
                }

                Date date = report.getCreated();
                dateView.setText(dateFormat.format(date));
            }

            return convertView;
        }
    }
}
