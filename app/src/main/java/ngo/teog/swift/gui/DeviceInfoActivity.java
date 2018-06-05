package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;

public class DeviceInfoActivity extends AppCompatActivity {

    private HospitalDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = this.getIntent();
        device = (HospitalDevice) intent.getSerializableExtra("device");

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

        ImageView statusImageView = findViewById(R.id.statusImageView);
        TextView statusTextView = findViewById(R.id.statusTextView);
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

            RequestFactory.DefaultRequest request = new RequestFactory().createDeviceImageRequest(this, progressBar, globalImageView, device.getID());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.GONE);

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
