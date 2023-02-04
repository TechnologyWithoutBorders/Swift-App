package ngo.teog.swift.gui.deviceCreation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Third step when creating a device: Take a picture of the device.
 * @author nitelow
 */
public class NewDeviceActivity3 extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;
    private ImageView imageView;

    private HospitalDevice device;

    private String imagePath = null;

    private boolean deviceCreated = false;

    @Inject
    ViewModelFactory viewModelFactory;

    private NewDeviceViewModel3 viewModel;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        imageView = findViewById(R.id.imageView);
        TextView orientationHint = findViewById(R.id.orientation_hint);

        if(savedInstanceState != null) {
            device = (HospitalDevice)savedInstanceState.getSerializable(ResourceKeys.DEVICE);
            imagePath = savedInstanceState.getString(ResourceKeys.IMAGE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            imageView.setVisibility(View.VISIBLE);
            orientationHint.setVisibility(View.INVISIBLE);
        } else {
            Intent intent = this.getIntent();
            device = (HospitalDevice)intent.getSerializableExtra(ResourceKeys.DEVICE);
        }

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        Drawable drawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.baseline_screen_rotation_24, null);

        if(drawable != null) {
            final float density = getResources().getDisplayMetrics().density;
            final int width = Math.round(30 * density);
            final int height = Math.round(30 * density);

            drawable.setBounds(0, 0, width, height);

            orientationHint.setCompoundDrawables(drawable, null, null, null);
        }

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(NewDeviceViewModel3.class);
        viewModel.init(userId, device.getId());

        viewModel.getUser().observe(this, user -> {
            if(user != null) {
                device.setHospital(user.getHospital());
                //TODO sicherstellen, dass das passiert ist, bevor es weitergeht
            }
        });

        viewModel.getDevice().observe(this, device -> {
            if(device != null) {
                //this event is sometimes dispatched multiple times, so we remember if we have already handled it
                if(!deviceCreated) {
                    deviceCreated = true;

                    Intent intent = new Intent(NewDeviceActivity3.this, DeviceInfoActivity.class);
                    intent.putExtra(ResourceKeys.DEVICE_ID, device.getDevice().getId());
                    startActivity(intent);

                    NewDeviceActivity3.this.finish();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    imageView.setImageBitmap(decode(imagePath));
                    imageView.setVisibility(View.VISIBLE);
                    orientationHint.setVisibility(View.INVISIBLE);
                }
            }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ResourceKeys.DEVICE, device);
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
            String targetName = device.getId() + ".jpg";

            File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);//TODO: maybe use cache dir as well?
            File image = new File(dir, targetName);

            try {
                File compressedImage = new Compressor(this)
                        .setMaxWidth(800)
                        .setMaxHeight(600)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(tempFile);

                try(FileInputStream source = new FileInputStream(compressedImage); FileOutputStream destination = new FileOutputStream(image)) {
                    FileChannel inputChannel = source.getChannel();
                    FileChannel outputChannel = destination.getChannel();

                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

                    SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

                    viewModel.createDevice(device, userId);
                } catch(Exception e) {
                    Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                } finally {
                    boolean deleted = compressedImage.delete();

                    if(!deleted) {
                        Log.w(this.getClass().getName(), "compressed image has not been deleted");
                    }
                }
            } catch(IOException e) {
                Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
            } finally {
                boolean deleted = tempFile.delete();

                if(!deleted) {
                    Log.w(this.getClass().getName(), "temporary image has not been deleted");
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_picture_attached), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile("image_" + device.getId(), ".jpg", storageDir);
    }

    private Bitmap decode(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    public void dispatchTakePictureIntent(View view) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File imageFile = createImageFile();
            imagePath = imageFile.getAbsolutePath();

            Uri photoURI = FileProvider.getUriForFile(this,"ngo.teog.swift.provider", imageFile);//TODO constant

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                activityResultLauncher.launch(takePictureIntent);
            }
        } catch(Exception e) {
            Log.e(this.getClass().getName(), "dispatching picture event failed", e);
            Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
        }
    }
}
