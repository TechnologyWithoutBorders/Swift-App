package ngo.teog.swift.gui.deviceCreation;

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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ImageUploader;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class NewDeviceActivity3 extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private final int REQUEST_IMAGE_CAPTURE = 100;
    private ImageView imageView;

    private HospitalDevice device;

    private String imagePath = null;

    @Inject
    ViewModelFactory viewModelFactory;

    private NewDeviceViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device3);

        imageView = findViewById(R.id.imageView);

        if(savedInstanceState != null) {
            device = (HospitalDevice)savedInstanceState.getSerializable(Defaults.DEVICE_KEY);
            imagePath = savedInstanceState.getString(Defaults.IMAGE_KEY);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            Intent intent = this.getIntent();
            device = (HospitalDevice)intent.getSerializableExtra(Defaults.DEVICE_KEY);
        }

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewDeviceViewModel.class);
        viewModel.init(userId, device.getId());

        viewModel.getUser().observe(this, user -> {
            if(user != null) {
                device.setHospital(user.getHospital());
                //TODO sicherstellen, dass das passiert ist, bevor es weitergeht
            }
        });

        viewModel.getDevice().observe(this, device -> {
            if(device != null) {
                Intent intent = new Intent(NewDeviceActivity3.this, DeviceInfoActivity.class);
                intent.putExtra(Defaults.DEVICE_ID_KEY, device.getDevice().getId());
                startActivity(intent);

                NewDeviceActivity3.this.finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Defaults.DEVICE_KEY, device);
        outState.putString(Defaults.IMAGE_KEY, imagePath);

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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.newdevice_activity_3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createDevice(View view) {
        if(imagePath != null) {
            nextButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            Bitmap decodedImage = decode(imagePath, 640);
            String targetName = device.getId() + ".jpg";

            FileOutputStream outputStream;

            try {
                File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                dir.mkdirs();

                outputStream = new FileOutputStream(new File(dir, targetName));
                decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                File tempFile = new File(imagePath);
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data imageData = new Data.Builder()
                    .putString(Defaults.PATH_KEY, targetName)
                    .putInt(Defaults.DEVICE_ID_KEY, device.getId())
                    .build();

            OneTimeWorkRequest uploadWork =
                    new OneTimeWorkRequest.Builder(ImageUploader.class)
                            .setConstraints(constraints)
                            .setInputData(imageData)
                            .build();

            WorkManager.getInstance(this).enqueue(uploadWork);

            SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
            int user = preferences.getInt(Defaults.ID_PREFERENCE, -1);

            viewModel.createDevice(device, user);
        } else {
            Toast.makeText(this, "no picture attached", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile("image_" + Integer.toString(device.getId()), ".jpg", storageDir);

        return image;
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
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageBitmap(decode(imagePath, 640));
        }
    }

    public void dispatchTakePictureIntent(View view) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File imageFile = createImageFile();
            imagePath = imageFile.getAbsolutePath();

            if(imageFile != null) {
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
            }
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
    }
}
