package ngo.teog.swift.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.io.InputStream;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;

public class DeviceInfoActivity extends AppCompatActivity {

    private HospitalDevice device;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        device = (HospitalDevice)intent.getSerializableExtra("device");

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(DeviceInfoActivity.this);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                View dialogView = getLayoutInflater().inflate(R.layout.image_dialog, null);
                ImageView imageView = dialogView.findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
                settingsDialog.setContentView(dialogView);
                settingsDialog.show();
            }
        });

        TextView statusView = findViewById(R.id.statusView);
        if(device.isWorking()) {
            statusView.setText(R.string.device_status_working);
            statusView.setBackgroundColor(Color.GREEN);
        } else {
            statusView.setText(R.string.device_status_broken);
            statusView.setBackgroundColor(Color.RED);
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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

            RequestFactory.DeviceImageRequest request = new RequestFactory().createDeviceImageRequest(this, progressBar, imageView, device.getID());

            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);

            queue.add(request);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_info, menu);
        return true;
    }

    public void startReportActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NewReportActivity.class);
        intent.putExtra("DEVICE", device);

        startActivity(intent);
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
}
