package ngo.teog.swift.gui.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseFragment;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class BarcodeFragment extends BaseFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private BarcodeViewModel viewModel;

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

                invokeFetch(deviceNumber);
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
        return inflater.inflate(R.layout.fragment_barcode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);
        barcodeScannerView.decodeContinuous(callback);

        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        searchField = view.findViewById(R.id.code_search_text);

        progressBar = view.findViewById(R.id.progressBar);

        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }

        DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BarcodeViewModel.class);
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

    public void invokeFetch(int deviceId) {
        searchField.setText(Integer.toString(deviceId));
        searchButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        viewModel.init(deviceId);
        viewModel.getDeviceInfo().observe(BarcodeFragment.this, deviceInfo -> {
            if(deviceInfo != null) {
                Intent intent = new Intent(BarcodeFragment.this.getContext(), DeviceInfoActivity.class);
                intent.putExtra(Defaults.DEVICE_KEY, deviceInfo);
                BarcodeFragment.this.startActivity(intent);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event);
    }

    public void search() {
        String searchString = searchField.getText().toString();

        try {
            int deviceNumber = Integer.parseInt(searchString);

            this.invokeFetch(deviceNumber);
        } catch(NumberFormatException e) {
            Toast.makeText(this.getContext().getApplicationContext(), "invalid device number", Toast.LENGTH_SHORT).show();
        }
    }
}
