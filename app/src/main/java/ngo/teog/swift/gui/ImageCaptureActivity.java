package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.deviceCreation.NewDeviceViewModel3;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Activity that is used to capture, decode and upload a device image.
 * @author nitelow
 */
//Todo should be generically usable for other images
public class ImageCaptureActivity extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private final int REQUEST_IMAGE_CAPTURE = 100;
    private ImageView imageView;

    private int deviceId;

    private String imagePath = null;

    @Inject
    ViewModelFactory viewModelFactory;

    private ImageCaptureViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_image_capture);

        imageView = findViewById(R.id.imageView);

        if(savedInstanceState != null) {
            deviceId = savedInstanceState.getInt(ResourceKeys.DEVICE_ID);
            imagePath = savedInstanceState.getString(ResourceKeys.IMAGE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            Intent intent = this.getIntent();
            deviceId = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);
        }

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(ImageCaptureViewModel.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ResourceKeys.DEVICE_ID, deviceId);
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

            File tempFile = new File(imagePath);
            String targetName = deviceId + ".jpg";

            File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
            File image = new File(dir, targetName);

            try(FileInputStream source = new FileInputStream(tempFile); FileOutputStream destination = new FileOutputStream(image)) {
                new Compressor(this)
                        .setMaxWidth(800)
                        .setMaxHeight(600)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(tempFile);

                FileChannel inputChannel = source.getChannel();
                FileChannel outputChannel = destination.getChannel();

                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

                source.close();
                destination.close();

                SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

                viewModel.updateDeviceImage(deviceId, userId);

                boolean deleted = tempFile.delete();

                if(!deleted) {
                    Log.w(this.getClass().getName(), "temporary file has not been deleted");
                }
            } catch(Exception e) {
                Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
            }

            this.finish();
        } else {
            Toast.makeText(this, getString(R.string.no_picture_attached), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile("image_" + deviceId, ".jpg", storageDir);
    }

    private Bitmap decode(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageBitmap(decode(imagePath));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void dispatchTakePictureIntent(View view) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File imageFile = createImageFile();
            imagePath = imageFile.getAbsolutePath();

            Uri photoURI = FileProvider.getUriForFile(this,"ngo.teog.swift.provider", imageFile);//TODO constant

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch(Exception e) {
            Log.e(this.getClass().getName(), "dispatching picture event failed", e);
            Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
        }
    }
}
