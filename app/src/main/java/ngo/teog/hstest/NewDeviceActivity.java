package ngo.teog.hstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import ngo.teog.hstest.comm.RequestFactory;
import ngo.teog.hstest.comm.VolleyManager;
import ngo.teog.hstest.helpers.HospitalDevice;

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

    public final static char CR  = (char) 0x0D;
    public final static char LF  = (char) 0x0A;

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
            bitmap = savedInstanceState.getParcelable("IMAGE");
            imageView.setImageBitmap(bitmap);
        }

        createButton = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        outState.putParcelable("IMAGE", bitmap);
        super.onSaveInstanceState(outState);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void createDevice(View view) {
        createButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        HospitalDevice device = new HospitalDevice(-1, assetNumberField.getText().toString(),
                typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), null);

        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

        RequestFactory factory = new RequestFactory();
        RequestFactory.DeviceCreationRequest request = factory.createDeviceCreationRequest(this, progressBar, createButton, device, bitmap, -1, wardField.getText().toString());

        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap)extras.get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}
