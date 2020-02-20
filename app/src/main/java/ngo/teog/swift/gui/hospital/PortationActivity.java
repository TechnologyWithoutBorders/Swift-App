package ngo.teog.swift.gui.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class PortationActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portation);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        //TODO Rechte überprüfen?

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        PortationViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(PortationViewModel.class);
        viewModel.init(id);
        viewModel.getHospitalDump().observe(this, hospitalDump -> {
            if(hospitalDump != null) {
                //can't use Intent.ACTION_CREATE_DOCUMENT as it requires new Android API level

                File file = new File(this.getCacheDir(), "swift_export.csv");

                try {
                    FileWriter fileWriter = new FileWriter(file);
                    CSVWriter writer = new CSVWriter(fileWriter);

                    Hospital hospital = hospitalDump.getHospital();

                    writer.writeNext(new String[]{"ID", "Name", "Location", "Longitude", "Latitude"});
                    writer.writeNext(new String[]{Integer.toString(hospital.getId()), hospital.getName(), hospital.getLocation(), Float.toString(hospital.getLongitude()), Float.toString(hospital.getLatitude())});

                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
