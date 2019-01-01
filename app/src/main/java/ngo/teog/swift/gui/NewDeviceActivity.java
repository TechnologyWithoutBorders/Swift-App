package ngo.teog.swift.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import ngo.teog.swift.R;

public class NewDeviceActivity extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private DecoratedBarcodeView barcodeScannerView;
    private String lastText;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();

            try {
                int deviceNumber = Integer.parseInt(result.getText());

                Intent intent = new Intent(NewDeviceActivity.this, NewDeviceActivity2.class);
                intent.putExtra("device_number", deviceNumber);

                startActivity(intent);
            } catch(NumberFormatException e) {
                //ignore
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            //ignore
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_creation1, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_device);

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        barcodeScannerView = findViewById(R.id.barcodeScannerView);
        barcodeScannerView.decodeContinuous(callback);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();
        lastText = null;
    }
}
