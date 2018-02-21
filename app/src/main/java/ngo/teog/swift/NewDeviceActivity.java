package ngo.teog.swift;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ngo.teog.swift.comm.RequestFactory;
import ngo.teog.swift.comm.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;

public class NewDeviceActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Bitmap bitmap;
    private Button createButton;
    private ProgressBar progressBar;

    private EditText assetNumberField;
    private EditText typeField;
    private EditText serialNumberField;
    private EditText manufacturerField;
    private EditText modelField;
    private EditText wardField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_device);

        imageView = findViewById(R.id.imageView);

        assetNumberField = findViewById(R.id.assetNumberText);
        typeField = findViewById(R.id.typeText);
        serialNumberField = findViewById(R.id.serialNumberText);
        manufacturerField = findViewById(R.id.manufacturerText);
        modelField = findViewById(R.id.modelText);
        wardField = findViewById(R.id.wardText);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey("IMAGE")) {
                String path = savedInstanceState.getString("IMAGE");
                imageView.setImageBitmap(decode(path, imageView.getHeight(), imageView.getWidth()));
            }
        }

        createButton = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mCurrentPhotoPath != null) {
            outState.putString("IMAGE", mCurrentPhotoPath);
        }
        super.onSaveInstanceState(outState);
    }

    public void dispatchTakePictureIntent(View view) {
        try {
            File imageFile = createImageFile();

            Uri photoURI = FileProvider.getUriForFile(this,
                    "ngo.teog.swift.fileprovider",
                    imageFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch(Exception e) {
            Log.e("ERROR", "", e);
        }
    }

    public void createDevice(View view) {
        createButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        HospitalDevice device = new HospitalDevice(-1, assetNumberField.getText().toString(),
                typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), null, true, new Date());

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        RequestFactory factory = new RequestFactory();
        RequestFactory.DeviceCreationRequest request = factory.createDeviceCreationRequest(this, progressBar, createButton, device, bitmap, wardField.getText().toString());

        queue.add(request);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap decode(String path, int targetH, int targetW) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            bitmap = decode(mCurrentPhotoPath, 500, 500);
            imageView.setImageBitmap(decode(mCurrentPhotoPath, imageView.getHeight(), imageView.getWidth()));
        }
    }
}
