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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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

public class DeviceInfoActivity extends AppCompatActivity {

    private ReportArrayAdapter adapter;

    private HospitalDevice device;
    private ImageView statusImageView;
    private TextView statusTextView;

    private ListView reportListView;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        device = (HospitalDevice) intent.getSerializableExtra("device");

        reportListView = findViewById(R.id.reportList);

        ArrayList<Report> values = new ArrayList<>();

        adapter = new ReportArrayAdapter(this, values);
        reportListView.setAdapter(adapter);

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

        statusImageView = findViewById(R.id.statusImageView);
        statusTextView = findViewById(R.id.statusTextView);
        if (device.isWorking()) {
            statusTextView.setText("device requires maintenance");
            statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_maintenance));
            statusImageView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else {
            statusTextView.setText("device requires repair");
            statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repair));
            statusImageView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }

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

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.DefaultRequest request = new RequestFactory().createDeviceImageRequest(this, progressBar, globalImageView, device.getID());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.GONE);

            queue.add(request);

            RequestFactory.ReportListRequest reportListRequest = new RequestFactory().createReportListRequest(this, reportListProgressbar, reportListView, null, adapter);

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

    private boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void showStatusDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change status");

        final Spinner input = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.device_states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input.setAdapter(adapter);
        input.setSelection(device.getState());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeviceInfoActivity.this, ReportCreationActivity.class);
                intent.putExtra("NEW_STATUS", input.getSelectedItemPosition());
                intent.putExtra("DEVICE", device.getID());
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

            TextView statusChangeView = convertView.findViewById(R.id.statusChangeView);
            TextView dateView = convertView.findViewById(R.id.dateView);

            Report report = this.getItem(position);

            if(report != null) {
                String statusString = HospitalDevice.STATES[report.getPreviousState()] + " -> " + HospitalDevice.STATES[report.getCurrentState()];

                statusChangeView.setText(statusString);

                String dateString = DATE_FORMAT.format(report.getDateTime());
                dateView.setText(dateString);
            }

            return convertView;
        }
    }
}
