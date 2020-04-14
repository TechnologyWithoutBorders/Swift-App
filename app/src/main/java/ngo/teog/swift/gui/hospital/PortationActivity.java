package ngo.teog.swift.gui.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;
import ngo.teog.swift.helpers.export.DeviceDump;

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
    }

    public void exportCSV(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("application/zip")
                .putExtra(Intent.EXTRA_TITLE, "swift_export.zip");

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null && data.getData() != null) {
                    Uri fileUri = data.getData();

                    SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

                    PortationViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(PortationViewModel.class);
                    viewModel.init(id);
                    viewModel.getHospitalDump().observe(this, hospitalDump -> {
                        if(hospitalDump != null) {
                            try {
                                ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(getContentResolver().openOutputStream(fileUri)));
                                CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOut));

                                Hospital hospital = hospitalDump.getHospital();

                                ZipEntry hospitalEntry = new ZipEntry("hospitals.csv");
                                zipOut.putNextEntry(hospitalEntry);

                                writer.writeNext(new String[] {"ID", "Name", "Location", "Longitude", "Latitude"});
                                writer.writeNext(new String[] {Integer.toString(hospital.getId()), hospital.getName(), hospital.getLocation(), Float.toString(hospital.getLongitude()), Float.toString(hospital.getLatitude())});

                                writer.flush();
                                zipOut.closeEntry();
                                ZipEntry userEntry = new ZipEntry("users.csv");
                                zipOut.putNextEntry(userEntry);

                                writer.writeNext(new String[] {"Hospital", "ID", "Name", "Position", "Mail", "Phone"});

                                for(User user : hospitalDump.getUsers()) {
                                    writer.writeNext(new String[] {Integer.toString(user.getHospital()), Integer.toString(user.getId()), user.getName(), user.getPosition(), user.getMail(), user.getPhone()});
                                }

                                writer.flush();
                                zipOut.closeEntry();
                                ZipEntry deviceEntry = new ZipEntry("devices.csv");
                                zipOut.putNextEntry(deviceEntry);

                                writer.writeNext(new String[] {"Hospital", "ID", "Asset Number", "Ward", "Type", "Manufacturer", "Model", "Serial Number"});

                                for(DeviceDump deviceDump : hospitalDump.getDeviceDumps()) {
                                    HospitalDevice device = deviceDump.getDevice();

                                    writer.writeNext(new String[] {Integer.toString(device.getHospital()), Integer.toString(device.getId()), device.getAssetNumber(), device.getWard(), device.getType(), device.getManufacturer(), device.getModel(), device.getSerialNumber()});
                                }

                                writer.flush();
                                zipOut.closeEntry();
                                ZipEntry reportEntry = new ZipEntry("reports.csv");
                                zipOut.putNextEntry(reportEntry);

                                writer.writeNext(new String[] {"ID", "Device", "Hospital", "Author", "Previous State", "Current State", "Description"});

                                for(DeviceDump deviceDump : hospitalDump.getDeviceDumps()) {
                                    for(Report report : deviceDump.getReports()) {
                                        DeviceStateVisuals oldVisuals = new DeviceStateVisuals(report.getPreviousState(), this);
                                        DeviceStateVisuals newVisuals = new DeviceStateVisuals(report.getCurrentState(), this);

                                        writer.writeNext(new String[] {Integer.toString(report.getId()), Integer.toString(report.getDevice()), Integer.toString(report.getHospital()), Integer.toString(report.getAuthor()), oldVisuals.getStateString(), newVisuals.getStateString(), report.getDescription()});
                                    }
                                }

                                writer.close();
                                zipOut.close();
                            } catch (IOException e) {
                                Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }
}
