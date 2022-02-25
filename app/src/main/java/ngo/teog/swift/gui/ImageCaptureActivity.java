package ngo.teog.swift.gui;

import android.content.Intent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;

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

            //Use different mechanisms depending on the SDK version
            if(Build.VERSION.SDK_INT >= 24) {
                photoURI = FileProvider.getUriForFile(this,"ngo.teog.swift.provider", imageFile);//TODO constant
            } else {
                photoURI = Uri.fromFile(imageFile);
            }

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
