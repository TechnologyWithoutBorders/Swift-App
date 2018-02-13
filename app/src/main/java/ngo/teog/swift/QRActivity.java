package ngo.teog.swift;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ngo.teog.swift.comm.RequestFactory;
import ngo.teog.swift.comm.VolleyManager;
import ngo.teog.swift.helpers.DeviceFilter;

public class QRActivity extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_qr, container, false);

        barcodeScannerView = rootView.findViewById(R.id.barcodeScannerView);
        barcodeScannerView.decodeContinuous(callback);

        searchButton = rootView.findViewById(R.id.search_button);
        searchField = rootView.findViewById(R.id.search_field);

        progressBar = rootView.findViewById(R.id.progressBar);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();
        lastText = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }*/

    public void search(View view) {
        String searchString = searchField.getText().toString();

        try {
            int deviceNumber = Integer.parseInt(searchString);

            this.invokeFetchRequest(deviceNumber);
        } catch(NumberFormatException e) {
            Toast.makeText(getActivity().getApplicationContext(), "invalid device number", Toast.LENGTH_SHORT).show();
        }
    }

    private void invokeFetchRequest(int id) {
        RequestQueue queue = VolleyManager.getInstance(getActivity()).getRequestQueue();
        DeviceFilter[] filters = {new DeviceFilter(DeviceFilter.ID, Integer.toString(id))};

        RequestFactory.DeviceOpenRequest request = new RequestFactory().createDeviceOpenRequest(getContext(), progressBar, searchButton, filters);

        searchButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);
    }
}
