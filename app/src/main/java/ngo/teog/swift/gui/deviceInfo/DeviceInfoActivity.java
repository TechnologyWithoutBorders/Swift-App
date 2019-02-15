package ngo.teog.swift.gui.deviceInfo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.ImageActivity;
import ngo.teog.swift.gui.ReportCreationActivity;
import ngo.teog.swift.gui.ReportInfoActivity;
import ngo.teog.swift.gui.userProfile.UserProfileViewModel;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.DeviceState;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class DeviceInfoActivity extends BaseActivity {

    private ReportArrayAdapter adapter;

    private ListView reportListView;

    private Spinner statusSpinner;

    private boolean triggered = false;

    private ProgressBar progressBar;
    private ImageView globalImageView;

    private TextView intervalView;
    private ProgressBar intervalProgressbar;

    @Inject
    ViewModelFactory viewModelFactory;
    private DeviceInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            triggered = savedInstanceState.getBoolean("TRIGGERED");
        }

        setContentView(R.layout.activity_device_info);

        intervalProgressbar = findViewById(R.id.intervalProgressbar);

        Intent intent = this.getIntent();
        int deviceId = intent.getIntExtra("device", -1);

        /*statusSpinner = findViewById(R.id.statusSpinner);
        statusSpinner.setAdapter(new StatusArrayAdapter(this, getResources().getStringArray(R.array.device_states)));
        statusSpinner.setSelection(device.getState());
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (triggered) {
                    Intent intent = new Intent(DeviceInfoActivity.this, ReportCreationActivity.class);
                    intent.putExtra("OLD_STATUS", device.getState());
                    intent.putExtra("NEW_STATUS", i);
                    intent.putExtra("DEVICE", device.getId());
                    startActivity(intent);
                } else {
                    triggered = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        reportListView = findViewById(R.id.reportList);

        ArrayList<Report> values = new ArrayList<>();

        adapter = new ReportArrayAdapter(this, values);
        reportListView.setAdapter(adapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
                intent.putExtra("REPORT", (Report) adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        globalImageView = findViewById(R.id.imageView);

        //TODO Bild nur auf Knopfdruck herunterladen, außerdem per Hash überprüfen, ob es ein neueres gibt

        progressBar = findViewById(R.id.progressBar);

        ProgressBar reportListProgressbar = findViewById(R.id.reportListProgressbar);

        TextView assetNumberView = findViewById(R.id.assetNumberView);
        TextView typeView = findViewById(R.id.typeView);
        TextView modelView = findViewById(R.id.modelView);
        TextView manufacturerView = findViewById(R.id.manufacturerView);
        TextView serialNumberView = findViewById(R.id.serialNumberView);

        TextView hospitalView = findViewById(R.id.hospitalView);
        //hospitalView.setText(device.getHospital());

        TextView wardView = findViewById(R.id.wardView);

        intervalView = findViewById(R.id.intervalView);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DeviceInfoViewModel.class);
        viewModel.init(deviceId);
        viewModel.getDevice().observe(this, device -> {
            if(device != null) {
                assetNumberView.setText(device.getAssetNumber());
                typeView.setText(device.getType());
                modelView.setText(device.getModel());
                manufacturerView.setText(device.getManufacturer());
                serialNumberView.setText(device.getSerialNumber());
                wardView.setText(device.getWard());

                int interval = device.getMaintenanceInterval();

                if(interval % 4 == 0) {
                    intervalView.setText(Integer.toString(interval/4) + " Months");
                } else {
                    intervalView.setText(Integer.toString(interval) + " Weeks");
                }

                File image = new File(getFilesDir(), "image_" + Integer.toString(device.getId()) + ".jpg");

                if(!image.exists()) {
                    globalImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_download_black_24dp));

                    globalImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadImage();
                        }
                    });
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    globalImageView.setImageBitmap(bitmap);
                    globalImageView.setBackgroundColor(Color.BLACK);

                    globalImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File image = new File(getFilesDir(), "image_" + Integer.toString(device.getId()) + ".jpg");

                            if(image.exists()) {
                                Intent intent = new Intent(DeviceInfoActivity.this, ImageActivity.class);
                                intent.putExtra("IMAGE", image);

                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

        /*if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.ReportListRequest reportListRequest = new RequestFactory().createReportListRequest(this, reportListProgressbar, reportListView, device.getId(), adapter);

            reportListProgressbar.setVisibility(View.VISIBLE);
            reportListView.setVisibility(View.INVISIBLE);

            queue.add(reportListRequest);
        }*/
    }

    public void editMaintenanceInterval(View view) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Maintenance Interval (Weeks)");

        final NumberPicker intervalPicker = new NumberPicker(this);
        intervalPicker.setMinValue(1);
        intervalPicker.setMaxValue(24);
        intervalPicker.setValue(device.getMaintenanceInterval());

        builder.setView(intervalPicker);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(DeviceInfoActivity.this.checkForInternetConnection()) {
                    RequestQueue queue = VolleyManager.getInstance(DeviceInfoActivity.this).getRequestQueue();

                    RequestFactory.DefaultRequest request = new RequestFactory().createDeviceUpdateRequest(DeviceInfoActivity.this, intervalProgressbar, intervalView, device.getId(), intervalPicker.getValue());

                    intervalProgressbar.setVisibility(View.VISIBLE);
                    intervalView.setVisibility(View.INVISIBLE);

                    queue.add(request);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();*/
    }

    private void downloadImage() {
        /*if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.DefaultRequest request = new RequestFactory().createDeviceImageRequest(this, progressBar, globalImageView, device.getId());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.GONE);

            queue.add(request);
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("TRIGGERED", false);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*// Handle item selection
        switch(item.getItemId()) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this device: http://teog.virlep.de/device/" + Integer.toString(device.getId()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share device link"));
                return true;
            case R.id.info:
                showInfo(R.string.deviceinfo_activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/

        return super.onOptionsItemSelected(item);
    }

    private class ReportArrayAdapter extends ArrayAdapter<Report> {
        private final Context context;

        private ReportArrayAdapter(Context context, ArrayList<Report> values) {
            super(context, -1, values);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_reports, parent, false);
            }

            Report report = this.getItem(position);

            if(report != null) {
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

                //authorView.setText(report.getAuthorName());

                long date = report.getCreated();
                dateView.setText(Long.toString(date));
            }

            return convertView;
        }
    }

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
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }
}
