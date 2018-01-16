package ngo.teog.hstest;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import ngo.teog.hstest.comm.RequestFactory;
import ngo.teog.hstest.comm.VolleyManager;
import ngo.teog.hstest.helpers.DeviceFilter;

public class QRActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;
    private String lastText;

    private Button searchButton;
    private EditText searchField;
    private ProgressBar progressBar;

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

                QRActivity.this.invokeFetchRequest(deviceNumber);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        barcodeScannerView = findViewById(R.id.barcodeScannerView);
        barcodeScannerView.decodeContinuous(callback);

        searchButton = findViewById(R.id.search_button);
        searchField = findViewById(R.id.search_field);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
        lastText = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void search(View view) {
        String searchString = searchField.getText().toString();

        try {
            int deviceNumber = Integer.parseInt(searchString);

            this.invokeFetchRequest(deviceNumber);
        } catch(NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "invalid device number", Toast.LENGTH_SHORT).show();
        }
    }

    private void invokeFetchRequest(int id) {
        RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();
        DeviceFilter[] filters = {new DeviceFilter(DeviceFilter.ID, Integer.toString(id))};

        RequestFactory.DeviceListRequest request = new RequestFactory().createDeviceRequest(this, progressBar, searchButton, filters, null);
        //TODO deviceRequest mit Öffnen

        searchButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);
    }
}
