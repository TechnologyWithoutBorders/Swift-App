package ngo.teog.swift.gui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.deviceInfo.DeviceInfoActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Part of the main activity, enables user to scan the barcodes of devices.
 * @author nitelow
 */
public class BarcodeFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private MainViewModel viewModel;

    private DecoratedBarcodeView barcodeScannerView;
    private String lastText;

    private Button searchButton;
    private ImageView torchButton;
    private EditText searchField;
    private ProgressBar progressBar;
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
        searchButton.setOnClickListener(sourceView -> search());

        torchButton = view.findViewById(R.id.torch_button);
        torchButton.setOnClickListener(sourceView -> switchTorchState());

        searchField = view.findViewById(R.id.code_search_text);

        progressBar = view.findViewById(R.id.progressBar);

        if(ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }

        DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.requireContext().getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel.init(id);
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

    private void invokeFetch(int deviceId) {
        searchField.setText(String.format(Locale.ROOT, "%d", deviceId));
        searchButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        viewModel.getDeviceInfo(deviceId).observe(BarcodeFragment.this.getViewLifecycleOwner(), deviceInfo -> {
            if(deviceInfo != null) {
                Intent intent = new Intent(BarcodeFragment.this.getContext(), DeviceInfoActivity.class);
                intent.putExtra(ResourceKeys.DEVICE_ID, deviceInfo.getDevice().getId());
                BarcodeFragment.this.startActivity(intent);
            } else {
                Toast.makeText(this.requireContext().getApplicationContext(), getString(R.string.device_not_found), Toast.LENGTH_SHORT).show();
            }

            searchField.setText(null);
            searchButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //This seems to be null sometimes??
        if(barcodeScannerView != null) {
            return barcodeScannerView.onKeyDown(keyCode, event);
        } else {
            return false;
        }
    }

    private void switchTorchState() {
        if(!torchIsOn){
            barcodeScannerView.setTorchOn();
            torchButton.setColorFilter(this.getResources().getColor(R.color.white));
            torchIsOn = true;
        } else {
            barcodeScannerView.setTorchOff();
            torchButton.setColorFilter(this.getResources().getColor(R.color.grey_table_bar));
            torchIsOn = false;
        }

    }

    private void search() {
        String searchString = searchField.getText().toString();

        try {
            int deviceNumber = Integer.parseInt(searchString);

            this.invokeFetch(deviceNumber);
        } catch(NumberFormatException e) {
            Toast.makeText(this.requireContext().getApplicationContext(), getString(R.string.device_number_invalid), Toast.LENGTH_SHORT).show();
        }
    }
}
