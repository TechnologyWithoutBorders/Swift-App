package ngo.teog.swift.gui;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;

public class NewDeviceActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private String mCurrentPhotoPath;
    private ProgressBar progressBar;

    private EditText assetNumberField;
    private EditText typeField;
    private EditText serialNumberField;
    private EditText manufacturerField;
    private EditText modelField;
    private EditText wardField;

    private ViewFlipper flipper;
    private LinearLayout first;
    private LinearLayout second;
    private LinearLayout third;
    private Button nextButton;

    private RelativeLayout buttonArea;

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

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        nextButton = findViewById(R.id.nextButton);
        flipper = findViewById(R.id.flipper);

        buttonArea = findViewById(R.id.buttonArea);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mCurrentPhotoPath != null) {
            imageView.setImageBitmap(decode(mCurrentPhotoPath, 500, 500));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey("IMAGE")) {
                mCurrentPhotoPath = savedInstanceState.getString("IMAGE");
            }
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

    public void next(View view) {
        if(flipper.getCurrentView() == first) {
            this.setTitle("Enter general data");
            buttonArea.setVisibility(View.VISIBLE);
            flipper.showNext();
        } else if(flipper.getCurrentView() == second) {
            this.setTitle("Attach Picture");
            nextButton.setText("Create");
            flipper.showNext();
        } else if(flipper.getCurrentView() == third) {
            nextButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            Bitmap bitmap = decode(mCurrentPhotoPath, 500, 500);

            HospitalDevice device = new HospitalDevice(-1, assetNumberField.getText().toString(),
                    typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), 0, new Date(), "bla", "blub", false);

            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory factory = new RequestFactory();
            RequestFactory.DefaultRequest request = factory.createDeviceCreationRequest(this, progressBar, nextButton, device, bitmap, wardField.getText().toString());

            queue.add(request);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);//TODO so landet das ja garnicht in der Gallerie

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
            imageView.setImageBitmap(decode(mCurrentPhotoPath, imageView.getHeight(), imageView.getWidth()));
        }
    }
}
