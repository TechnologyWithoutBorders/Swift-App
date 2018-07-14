package ngo.teog.swift.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.Report;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.filters.Filter;
import ngo.teog.swift.helpers.filters.ReportFilter;

public class DeviceInfoActivity extends AppCompatActivity {

    private ReportArrayAdapter adapter;

    private HospitalDevice device;

    private ListView reportListView;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Spinner statusSpinner;

    private boolean triggered = false;

    private boolean notifications = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        device = (HospitalDevice) intent.getSerializableExtra("device");

        statusSpinner = findViewById(R.id.statusSpinner);
        statusSpinner.setAdapter(new StatusArrayAdapter(this, getResources().getStringArray(R.array.device_states)));
        statusSpinner.setSelection(device.getState());
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(triggered) {
                    Intent intent = new Intent(DeviceInfoActivity.this, ReportCreationActivity.class);
                    intent.putExtra("OLD_STATUS", device.getState());
                    intent.putExtra("NEW_STATUS", i);
                    intent.putExtra("DEVICE", device.getID());
                    startActivity(intent);
                } else {
                    triggered = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        reportListView = findViewById(R.id.reportList);

        ArrayList<Report> values = new ArrayList<>();

        adapter = new ReportArrayAdapter(this, values);
        reportListView.setAdapter(adapter);

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DeviceInfoActivity.this, ReportInfoActivity.class);
                intent.putExtra("REPORT", (Report)adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        final ImageView globalImageView = findViewById(R.id.imageView);
        globalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File image = null;//TODO außerdem deleteOnExit() nutzen?
                try {
                    image = File.createTempFile(imageFileName,".jpg", DeviceInfoActivity.this.getCacheDir());

                    FileOutputStream fos = null;
                    fos = new FileOutputStream(image);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    ((BitmapDrawable)globalImageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);

                    fos.close();

                    // Save a file: path for use with ACTION_VIEW intents
                    String mCurrentPhotoPath = image.getAbsolutePath();

                    Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                    intent.putExtra("IMAGE", mCurrentPhotoPath);

                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //TODO Bild nur auf Knopfdruck herunterladen, außerdem per Hash überprüfen, ob es ein neueres gibt

        ProgressBar progressBar = findViewById(R.id.progressBar);

        ProgressBar reportListProgressbar = findViewById(R.id.reportListProgressbar);

        TextView assetNumberView = findViewById(R.id.assetNumberView);
        assetNumberView.setText(device.getAssetNumber());

        TextView typeView = findViewById(R.id.typeView);
        typeView.setText(device.getType());

        TextView modelView = findViewById(R.id.modelView);
        modelView.setText(device.getModel());

        TextView manufacturerView = findViewById(R.id.manufacturerView);
        manufacturerView.setText(device.getManufacturer());

        TextView serialNumberView = findViewById(R.id.serialNumberView);
        serialNumberView.setText(device.getSerialNumber());

        TextView hospitalView = findViewById(R.id.hospitalView);
        hospitalView.setText(device.getHospital());

        TextView wardView = findViewById(R.id.wardView);
        wardView.setText(device.getWard());

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.DefaultRequest request = new RequestFactory().createDeviceImageRequest(this, progressBar, globalImageView, device.getID());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.GONE);

            queue.add(request);

            Filter[] filters = {new Filter(ReportFilter.DEVICE, Integer.toString(device.getID()))};
            RequestFactory.ReportListRequest reportListRequest = new RequestFactory().createReportListRequest(this, reportListProgressbar, reportListView, filters, adapter);

            reportListProgressbar.setVisibility(View.VISIBLE);
            reportListView.setVisibility(View.INVISIBLE);

            queue.add(reportListRequest);
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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.notificationButton:
                toggleNotifications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
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
                TextView dateView = convertView.findViewById(R.id.dateView);
                ImageView fromState = convertView.findViewById(R.id.fromState);

                int background = android.R.color.white;

                switch(report.getPreviousState()) {
                    case HospitalDevice.STATE_WORKING:
                        background = android.R.color.holo_green_dark;
                        break;
                    case HospitalDevice.STATE_PM_DUE:
                        background = android.R.color.holo_blue_light;
                        break;
                    case HospitalDevice.STATE_REPAIR_NEEDED:
                        background = android.R.color.holo_orange_dark;
                        break;
                    case HospitalDevice.STATE_IN_PROGRESS:
                        background = android.R.color.holo_green_light;
                        break;
                    case HospitalDevice.STATE_BROKEN_SALVAGE:
                        background = android.R.color.holo_red_dark;
                        break;
                    case HospitalDevice.STATE_WORKING_WITH_LIMITATIONS:
                        background = android.R.color.holo_red_light;
                        break;
                }

                fromState.setColorFilter(getResources().getColor(background));

                ImageView toState = convertView.findViewById(R.id.toState);

                switch(report.getCurrentState()) {
                    case HospitalDevice.STATE_WORKING:
                        background = android.R.color.holo_green_dark;
                        break;
                    case HospitalDevice.STATE_PM_DUE:
                        background = android.R.color.holo_blue_light;
                        break;
                    case HospitalDevice.STATE_REPAIR_NEEDED:
                        background = android.R.color.holo_orange_dark;
                        break;
                    case HospitalDevice.STATE_IN_PROGRESS:
                        background = android.R.color.holo_green_light;
                        break;
                    case HospitalDevice.STATE_BROKEN_SALVAGE:
                        background = android.R.color.holo_red_dark;
                        break;
                    case HospitalDevice.STATE_WORKING_WITH_LIMITATIONS:
                        background = android.R.color.holo_red_light;
                        break;
                }

                toState.setColorFilter(getResources().getColor(background));

                String dateString = DATE_FORMAT.format(report.getDateTime());
                dateView.setText(dateString);
            }

            return convertView;
        }
    }

    private void toggleNotifications() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to unsubscribe from this device?\nYou will no longer receive notifications abouts its state.")
                .setTitle("Confirm")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(DeviceInfoActivity.this, "not implemented yet", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ignore
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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

            int background = android.R.color.white;
            int drawable = R.drawable.ic_repair;

            switch(position) {
                case HospitalDevice.STATE_WORKING:
                    drawable = R.drawable.ic_check;
                    background = android.R.color.holo_green_dark;
                    break;
                case HospitalDevice.STATE_PM_DUE:
                    drawable = R.drawable.ic_maintenance;
                    background = android.R.color.holo_blue_light;
                    break;
                case HospitalDevice.STATE_REPAIR_NEEDED:
                    drawable = R.drawable.ic_repair;
                    background = android.R.color.holo_orange_dark;
                    break;
                case HospitalDevice.STATE_IN_PROGRESS:
                    drawable = R.drawable.ic_in_progress;
                    background = android.R.color.holo_green_light;
                    break;
                case HospitalDevice.STATE_BROKEN_SALVAGE:
                    drawable = R.drawable.ic_broken_salvage;
                    background = android.R.color.holo_red_dark;
                    break;
                case HospitalDevice.STATE_WORKING_WITH_LIMITATIONS:
                    drawable = R.drawable.ic_working_with_limitations;
                    background = android.R.color.holo_red_light;
                    break;
            }

            statusImageView.setImageDrawable(getResources().getDrawable(drawable));
            statusImageView.setBackgroundColor(getResources().getColor(background));

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
