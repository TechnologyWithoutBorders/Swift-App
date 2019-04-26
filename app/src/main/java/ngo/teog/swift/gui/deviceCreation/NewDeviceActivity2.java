package ngo.teog.swift.gui.deviceCreation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.data.HospitalDevice;

public class NewDeviceActivity2 extends BaseActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private int deviceNumber;

    private EditText assetNumberField;
    private EditText typeField;
    private EditText serialNumberField;
    private EditText manufacturerField;
    private EditText modelField;
    private EditText wardField;

    private NumberPicker intervalPicker;
    private Spinner weekMonthSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device2);

        assetNumberField = findViewById(R.id.assetNumberText);
        typeField = findViewById(R.id.typeText);
        serialNumberField = findViewById(R.id.serialNumberText);
        manufacturerField = findViewById(R.id.manufacturerText);
        modelField = findViewById(R.id.modelText);
        wardField = findViewById(R.id.wardText);

        intervalPicker = findViewById(R.id.intervalPicker);
        weekMonthSpinner = findViewById(R.id.spinner2);

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        NumberPicker intervalPicker = findViewById(R.id.intervalPicker);
        intervalPicker.setMinValue(1);
        intervalPicker.setMaxValue(24);
        intervalPicker.setValue(4);

        Intent intent = this.getIntent();
        deviceNumber = intent.getIntExtra("device_number", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_creation2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
                case R.id.info:
                showInfo(R.string.newdevice_activity_2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createDevice(View view) {
        if(typeField.getText().length() > 0) {
            if(serialNumberField.getText().length() > 0) {
                if(manufacturerField.getText().length() > 0) {
                    if(modelField.getText().length() > 0) {
                        nextButton.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        String assetNumber = assetNumberField.getText().toString();

                        if(assetNumber.length() == 0) {
                            assetNumber = Integer.toString(deviceNumber);
                        }

                        int interval;

                        if(((String)weekMonthSpinner.getSelectedItem()).equals("Week")) {
                            interval = intervalPicker.getValue();
                        } else {
                            interval = intervalPicker.getValue()*4;
                        }

                        HospitalDevice device = new HospitalDevice(deviceNumber, assetNumber,
                                typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), wardField.getText().toString(), 0, interval, System.currentTimeMillis());

                        Intent intent = new Intent(NewDeviceActivity2.this, NewDeviceActivity3.class);
                        intent.putExtra("device", device);

                        startActivity(intent);
                    } else {
                        modelField.setError("empty model");
                    }
                } else {
                    manufacturerField.setError("empty manufacturer");
                }
            } else {
                serialNumberField.setError("empty serial number");
            }
        } else {
            typeField.setError("empty type");
        }
    }
}
