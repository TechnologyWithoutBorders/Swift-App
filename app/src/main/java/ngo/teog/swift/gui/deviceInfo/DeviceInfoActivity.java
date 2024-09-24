package ngo.teog.swift.gui.deviceInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.communication.BaseErrorListener;
import ngo.teog.swift.communication.BaseResponseListener;
import ngo.teog.swift.communication.DataAction;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.SwiftResponse;
import ngo.teog.swift.communication.TransparentServerException;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.ImageActivity;
import ngo.teog.swift.gui.deviceCreation.NewDeviceActivity2;
import ngo.teog.swift.gui.reportInfo.ReportInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.OrganizationalUnit;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;
import ngo.teog.swift.helpers.filters.DeviceAttribute;

/**
 * Shows all available information about a device.
 * @author nitelow
 */
public class DeviceInfoActivity extends BaseActivity {

    private boolean resumed = false;

    private static final int ASSET_NUMBER = 0;
    private static final int TYPE = 1;
    private static final int MODEL = 2;
    private static final int MANUFACTURER = 3;
    private static final int SERIAL_NUMBER = 4;
    private static final int ORG_UNIT = 5;
    private static final int MAINTENANCE_INTERVAL = 6;

    private static final String[] PARAM_TITLES = {"Asset Number", "Type", "Model", "Manufacturer", "Serial Number", "Department", "Maintenance Interval (Months)"};//TODO: Constants

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

    private TextView assetNumberView, typeView, modelView, manufacturerView, serialNumberView, orgUnitView, intervalView;

    private List<OrganizationalUnit> orgUnits;
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
        reportListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        reportListView.setStackFromBottom(true);

        reportListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Report report = ((ReportInfo)adapterView.getItemAtPosition(i)).getReport();

            Intent intent1 = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
            intent1.putExtra(ResourceKeys.DEVICE_ID, report.getDevice());
            intent1.putExtra(ResourceKeys.HOSPITAL_ID, deviceInfo.getHospital().getId());
            intent1.putExtra(ResourceKeys.REPORT_ID, report.getId());
            startActivity(intent1);
        });

        dummyImageView = findViewById(R.id.downloadImageView);
        globalImageView = findViewById(R.id.imageView);
        globalImageView.setBackgroundColor(Color.BLACK);

        globalImageView.setOnClickListener(view -> {
            File dir = new File(this.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
            boolean created = dir.mkdirs();

            if(created) {
                Log.v(this.getClass().getName(), "image directory has been created");
            }

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

        orgUnitView = findViewById(R.id.locationView);

        intervalView = findViewById(R.id.intervalView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(DeviceInfoViewModel.class);
        viewModel.init(userId, deviceId).observe(this, observable -> viewModel.refreshDevice());
        viewModel.getOrgUnits().observe(this, orgUnits -> {
            if(orgUnits != null) {
                orgUnits.sort(Comparator.comparing(OrganizationalUnit::getName));
                orgUnits.add(0, null);

                this.orgUnits = orgUnits;
            }
        });
        viewModel.getDeviceInfo().observe(this, deviceInfo -> {
            this.deviceInfo = deviceInfo;

            if(deviceInfo != null) {
                HospitalDevice device = deviceInfo.getDevice();

                List<ReportInfo> reports = deviceInfo.getReports();

                reports.sort(Comparator.comparingInt(reportInfo -> reportInfo.getReport().getId()));

                reportCreationButton.setOnClickListener((view) -> {
                    Intent intent1 = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
                    intent1.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                    intent1.putExtra(ResourceKeys.HOSPITAL_ID, deviceInfo.getHospital().getId());
                    startActivity(intent1);
                });

                DeviceStateVisuals visuals = new DeviceStateVisuals(reports.get(reports.size()-1).getReport().getCurrentState(), this);

                stateTextView.setText(visuals.getStateString());
                stateImageView.setImageDrawable(visuals.getStateIcon());
                stateImageView.setBackgroundColor(visuals.getBackgroundColor());

                assetNumberView.setText(device.getAssetNumber());
                typeView.setText(device.getType());
                modelView.setText(device.getModel());
                manufacturerView.setText(device.getManufacturer());
                serialNumberView.setText(device.getSerialNumber());

                OrganizationalUnit orgUnit = deviceInfo.getOrganizationalUnit();

                if(orgUnit != null) {
                    orgUnitView.setText(deviceInfo.getOrganizationalUnit().getName());
                }

                int interval = device.getMaintenanceInterval();
                intervalView.setText(getResources().getQuantityString(R.plurals.months_count, (interval/4), (interval/4)));

                File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                boolean created = dir.mkdirs();

                if(created) {
                    Log.v(this.getClass().getName(), "image directory has been created");
                }

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

        if(resumed) {
            Log.i(this.getClass().getName(), "activity has resumed, refreshing...");
            refresh();
        } else {
            resumed = true;
        }
    }

    private void refresh() {
        viewModel.refreshDevice();
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

    public void editOrgUnit(View view) {
        this.edit(ORG_UNIT, orgUnitView.getText().toString(), 25);
    }

    public void editMaintenanceInterval(View view) {
        this.edit(MAINTENANCE_INTERVAL, null, 0);
    }

    private void edit(int parameter, String presetText, int maxLength) {
        if(deviceInfo != null) {
            String titleString = PARAM_TITLES[parameter];

            final View editView;

            if(parameter == MAINTENANCE_INTERVAL) {
                NumberPicker numberPicker = new NumberPicker(this);
                numberPicker.setMinValue(NewDeviceActivity2.MIN_MAINT_INTERVAL);
                numberPicker.setMaxValue(NewDeviceActivity2.MAX_MAINT_INTERVAL);
                numberPicker.setValue(deviceInfo.getDevice().getMaintenanceInterval() / 4);

                editView = numberPicker;
            } else if(parameter == ORG_UNIT) {
                Spinner orgUnitSpinner = new Spinner(this);

                class OrgUnitAdapter extends ArrayAdapter<OrganizationalUnit> {
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

                        if(orgUnitSpinner.getSelectedItemPosition() == position) {
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

                orgUnitSpinner.setAdapter(new OrgUnitAdapter(this, orgUnits));

                int previousOrgUnit = 0;

                if(deviceInfo.getDevice().getOrganizationalUnit() != null) {
                    for (int i = 0; i < orgUnits.size(); i++) {
                        OrganizationalUnit reference = orgUnits.get(i);

                        if (reference != null && reference.getId() == deviceInfo.getDevice().getOrganizationalUnit()) {
                            previousOrgUnit = i;
                            break;
                        }
                    }
                }

                orgUnitSpinner.setSelection(previousOrgUnit);

                editView = orgUnitSpinner;
            } else {
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
                case ORG_UNIT:
                    OrganizationalUnit orgUnit = (OrganizationalUnit)(((Spinner) editView).getSelectedItem());

                    if(orgUnit != null) {
                        orgUnitView.setText(orgUnit.getName());
                        device.setOrganizationalUnit(orgUnit.getId());
                    } else {
                        orgUnitView.setText("");
                        device.setOrganizationalUnit(null);
                    }

                    break;
                case MAINTENANCE_INTERVAL:
                    int interval = ((NumberPicker) editView).getValue();

                    device.setMaintenanceInterval(interval*4);

                    intervalView.setText(getResources().getQuantityString(R.plurals.months_count, interval, interval));
                    break;
            }

            device.setLastUpdate(new Date());
            viewModel.updateDevice(device);
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
        if(item.getItemId() == R.id.share) {
            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            Intent intent = new Intent(Intent.ACTION_SEND);

            String assetString = getString(R.string.device).toLowerCase();
            String sharingString = String.format(getString(R.string.want_to_show), assetString, Defaults.HOST, assetString, preferences.getString(Defaults.COUNTRY_PREFERENCE, null), deviceInfo.getHospital().getId());
            intent.putExtra(Intent.EXTRA_TEXT,sharingString + deviceInfo.getDevice().getId());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share_link)));

            return true;
        } else if(item.getItemId() == R.id.info) {
            //Show tutorial
            FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                    .add(buildTutorialStep(attributeTable, getString(R.string.device_info_tutorial_attribute_table), Gravity.TOP))
                    .add(buildTutorialStep(stateSection, getString(R.string.device_info_tutorial_state_section), Gravity.TOP))
                    .add(buildTutorialStep(reportListView, getString(R.string.device_info_tutorial_report_list), Gravity.CENTER))
                    .add(buildTutorialStep(reportCreationButton, getString(R.string.device_info_tutorial_report_creation), Gravity.CENTER))
                    .add(buildTutorialStep(documentButton, getString(R.string.device_info_tutorial_documents), Gravity.CENTER));

            tutorialQueue.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchDocuments(View view) {
        if(this.checkForInternetConnection()) {
            documentButton.setVisibility(View.INVISIBLE);
            documentProgressBar.setVisibility(View.VISIBLE);

            Map<String, String> params = RequestFactory.generateParameterMap(this, DataAction.GET_COUNTRIES, false);
            params.put(DeviceAttribute.MANUFACTURER, deviceInfo.getDevice().getManufacturer());
            params.put(DeviceAttribute.MODEL, deviceInfo.getDevice().getModel());
            JSONObject jsonRequest = new JSONObject(params);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,//TODO: use get
                    Defaults.BASE_URL + Defaults.DOCUMENTS_URL,
                    jsonRequest,
                    new BaseResponseListener(this) {
                        @Override
                        public void onSuccess(JSONObject response) throws JSONException {
                            documentProgressBar.setVisibility(View.INVISIBLE);
                            documentButton.setVisibility(View.VISIBLE);

                            //The response provides a list of links to matching documents.
                            JSONArray documentList = response.getJSONArray(SwiftResponse.DATA_FIELD);

                            ArrayAdapter<String> documentAdapter = new ArrayAdapter<>(DeviceInfoActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>());

                            for(int i = 0; i < documentList.length(); i++) {
                                String docLink = documentList.getString(i);

                                documentAdapter.add(docLink);
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                            builder.setTitle(getString(R.string.documents_overview))
                                    .setPositiveButton("close", (dialogInterface, i) -> dialogInterface.cancel())
                                    .setSingleChoiceItems(documentAdapter, -1, (dialogInterface, i) -> {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Defaults.HOST + Defaults.INTERFACE_PATH + Defaults.DOCUMENTS_PATH + deviceInfo.getDevice().getManufacturer() + "/" + deviceInfo.getDevice().getModel() + "/" + documentAdapter.getItem(i))));
                                        dialogInterface.dismiss();
                                    });

                            AlertDialog dialog = builder.create();

                            dialog.show();
                        }

                        @Override
                        public void onError(Exception e) {
                            documentProgressBar.setVisibility(View.INVISIBLE);
                            documentButton.setVisibility(View.VISIBLE);

                            if(e instanceof TransparentServerException) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                                builder.setTitle(e.getMessage())
                                        .setPositiveButton("close", (dialogInterface, i) -> dialogInterface.cancel());

                                AlertDialog dialog = builder.create();

                                dialog.show();
                            } else {
                                Toast.makeText(getApplicationContext(), getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new BaseErrorListener(this) {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);

                            documentProgressBar.setVisibility(View.INVISIBLE);
                            documentButton.setVisibility(View.VISIBLE);
                        }
                    }
            );

            VolleyManager.getInstance(this).getRequestQueue().add(request);
        } else {
            Toast.makeText(this, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adapter for displaying the recent report list.
     */
    private static class ReportArrayAdapter extends ArrayAdapter<ReportInfo> {
        private final Context context;
        private final DateFormat dateFormat = new SimpleDateFormat(Defaults.DATE_PATTERN, Locale.getDefault());

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

                ImageView toState = convertView.findViewById(R.id.toState);

                DeviceStateVisuals stateVisuals = new DeviceStateVisuals(report.getCurrentState(),this.getContext());

                toState.setImageDrawable(stateVisuals.getStateIcon());
                toState.setColorFilter(stateVisuals.getBackgroundColor());

                String title = reportInfo.getReport().getTitle();

                if(!title.isEmpty()) {
                    titleView.setText(title);
                } else {
                    titleView.setText(reportInfo.getAuthor().getName());
                }

                Date date = report.getCreated();
                dateView.setText(dateFormat.format(date));
            }

            return convertView;
        }
    }
}
