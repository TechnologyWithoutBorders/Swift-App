package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ImageUploader;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class ImageCaptureActivity extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private final int REQUEST_IMAGE_CAPTURE = 100;
    private ImageView imageView;

    private int device;

    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_image_capture);

        imageView = findViewById(R.id.imageView);

        if(savedInstanceState != null) {
            device = savedInstanceState.getInt(ResourceKeys.DEVICE_ID);
            imagePath = savedInstanceState.getString(ResourceKeys.IMAGE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            Intent intent = this.getIntent();
            device = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        }

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ResourceKeys.DEVICE_ID, device);
        outState.putString(ResourceKeys.IMAGE, imagePath);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_creation3, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.newdevice_activity_3);
    }

    public void createDevice(View view) {
        if(imagePath != null) {
            nextButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            Bitmap decodedImage = decode(imagePath, 640);
            String targetName = device + ".jpg";

            FileOutputStream outputStream;

            try {
                File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                File image = new File(dir, targetName);

                outputStream = new FileOutputStream(image);
                decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                File tempFile = new File(imagePath);
                tempFile.delete();

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                Data imageData = new Data.Builder()
                        .putString(ResourceKeys.PATH, targetName)
                        .putInt(ResourceKeys.DEVICE_ID, device)
                        .build();

                OneTimeWorkRequest uploadWork =
                        new OneTimeWorkRequest.Builder(ImageUploader.class)
                                .setConstraints(constraints)
                                .setInputData(imageData)
                                .build();

                WorkManager.getInstance(getApplicationContext()).enqueue(uploadWork);
            } catch(Exception e) {
                Log.e("IMAGE_UPLOAD", "not created", e);
            }

            this.finish();
        } else {
            Toast.makeText(this, "no picture attached", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile("image_" + device, ".jpg", storageDir);
    }

    private Bitmap decode(String filePath, int targetW) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetW);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageBitmap(decode(imagePath, 800));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void dispatchTakePictureIntent(View view) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File imageFile = createImageFile();
            imagePath = imageFile.getAbsolutePath();

            Uri photoURI;

            if(Build.VERSION.SDK_INT >= 24) {
                photoURI = FileProvider.getUriForFile(this,"ngo.teog.swift.provider", imageFile);
            } else {
                photoURI = Uri.fromFile(imageFile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
    }
}
