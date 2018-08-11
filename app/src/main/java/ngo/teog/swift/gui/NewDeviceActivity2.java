package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;

import java.util.Date;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalDevice;

public class NewDeviceActivity2 extends AppCompatActivity {

    private Button nextButton;
    private ProgressBar progressBar;

    private int deviceNumber;

    private EditText assetNumberField;
    private EditText typeField;
    private EditText serialNumberField;
    private EditText manufacturerField;
    private EditText modelField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device2);

        assetNumberField = findViewById(R.id.assetNumberText);
        typeField = findViewById(R.id.typeText);
        serialNumberField = findViewById(R.id.serialNumberText);
        manufacturerField = findViewById(R.id.manufacturerText);
        modelField = findViewById(R.id.modelText);

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

                        HospitalDevice device = new HospitalDevice(deviceNumber, assetNumber,
                                typeField.getText().toString(), serialNumberField.getText().toString(), manufacturerField.getText().toString(), modelField.getText().toString(), 0, "bla", 4, new Date());

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
