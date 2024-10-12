package ngo.teog.swift.gui.deviceCreation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.ResourceKeys;

/**
 * First step when creating a device: Scan the corresponding barcode.
 * @author nitelow
 */
public class NewDeviceActivity extends BaseActivity {

    private ImageView torchButton;
    private DecoratedBarcodeView barcodeScannerView;
    private String lastText;
    private boolean torchIsOn = false;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();

            try {
                int deviceNumber = Integer.parseInt(result.getText());

                if(deviceNumber >= 0) {
                    Intent intent = new Intent(NewDeviceActivity.this, NewDeviceActivity2.class);
                    intent.putExtra(ResourceKeys.DEVICE_ID, deviceNumber);

                    startActivity(intent);
                    NewDeviceActivity.this.finish();
                } else {
                    Toast.makeText(NewDeviceActivity.this, getString(R.string.device_number_invalid), Toast.LENGTH_SHORT).show();
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, R.string.newdevice_activity_1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_device);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        torchButton = findViewById(R.id.torch_button);
        torchButton.setOnClickListener(sourceView -> switchTorchState());

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

    private void switchTorchState() {
        if(!torchIsOn){
            barcodeScannerView.setTorchOn();
            torchButton.setColorFilter(this.getResources().getColor(R.color.white, this.getTheme()));
            torchIsOn = true;
        } else {
            barcodeScannerView.setTorchOff();
            torchButton.setColorFilter(this.getResources().getColor(R.color.black, this.getTheme()));
            torchIsOn = false;
        }
    }
}
