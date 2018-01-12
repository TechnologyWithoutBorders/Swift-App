package ngo.teog.hstest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ngo.teog.hstest.helpers.Defaults;
import ngo.teog.hstest.helpers.HospitalDevice;

public class NewReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        Intent intent = this.getIntent();
        HospitalDevice device = (HospitalDevice)intent.getSerializableExtra("DEVICE");

        TextView deviceNumberView = findViewById(R.id.deviceNumberView);
        deviceNumberView.setText(Integer.toString(device.getID()));

        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(device.getAssetNumber());

        TextView typeView = findViewById(R.id.typeView);
        typeView.setText(device.getType());
    }
}
