package ngo.teog.swift.gui.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
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

/**
 * Provides import and export functionalities.
 * @author nitelow
 */
public class PortationActivity extends AppCompatActivity {

    private final String SYNC_DATA_FILE_NAME = "synchronisation.txt";

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
                .putExtra(Intent.EXTRA_TITLE, Defaults.EXPORT_FILE_NAME);

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

                    PortationViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(PortationViewModel.class);
                    viewModel.init(id);
                    viewModel.getHospitalDump().observe(this, hospitalDump -> {
                        if(hospitalDump != null) {
                            try {
                                //TODO streams/writers should be closed in "finally"-block
                                ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(getContentResolver().openOutputStream(fileUri)));
                                CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(zipOut));
                                PrintWriter printWriter = new PrintWriter(zipOut);

                                Hospital hospital = hospitalDump.getHospital();

                                ZipEntry hospitalEntry = new ZipEntry("hospitals.csv");
                                zipOut.putNextEntry(hospitalEntry);

                                csvWriter.writeNext(new String[] {"ID", "Name", "Location", "Longitude", "Latitude"});
                                csvWriter.writeNext(new String[] {Integer.toString(hospital.getId()), hospital.getName(), hospital.getLocation(), Float.toString(hospital.getLongitude()), Float.toString(hospital.getLatitude())});

                                csvWriter.flush();
                                zipOut.closeEntry();
                                ZipEntry userEntry = new ZipEntry("users.csv");
                                zipOut.putNextEntry(userEntry);

                                csvWriter.writeNext(new String[] {"Hospital", "ID", "Name", "Position", "Mail", "Phone"});

                                for(User user : hospitalDump.getUsers()) {
                                    csvWriter.writeNext(new String[] {Integer.toString(user.getHospital()), Integer.toString(user.getId()), user.getName(), user.getPosition(), user.getMail(), user.getPhone()});
                                }

                                csvWriter.flush();
                                zipOut.closeEntry();
                                ZipEntry deviceEntry = new ZipEntry("devices.csv");
                                zipOut.putNextEntry(deviceEntry);

                                csvWriter.writeNext(new String[] {"Hospital", "ID", "Asset Number", "Location", "Type", "Manufacturer", "Model", "Serial Number"});

                                for(DeviceDump deviceDump : hospitalDump.getDeviceDumps()) {
                                    HospitalDevice device = deviceDump.getDevice();

                                    csvWriter.writeNext(new String[] {Integer.toString(device.getHospital()), Integer.toString(device.getId()), device.getAssetNumber(), device.getLocation(), device.getType(), device.getManufacturer(), device.getModel(), device.getSerialNumber()});
                                }

                                csvWriter.flush();
                                zipOut.closeEntry();
                                ZipEntry reportEntry = new ZipEntry("reports.csv");
                                zipOut.putNextEntry(reportEntry);

                                csvWriter.writeNext(new String[] {"ID", "Device", "Hospital", "Author", "Title", "Previous State", "Current State", "Description"});

                                for(DeviceDump deviceDump : hospitalDump.getDeviceDumps()) {
                                    for(Report report : deviceDump.getReports()) {
                                        DeviceStateVisuals newVisuals = new DeviceStateVisuals(report.getCurrentState(), this);

                                        csvWriter.writeNext(new String[] {Integer.toString(report.getId()), Integer.toString(report.getDevice()), Integer.toString(report.getHospital()), Integer.toString(report.getAuthor()), report.getTitle(), newVisuals.getStateString(), report.getDescription()});
                                    }
                                }

                                //Add file with infos about last synchronisation
                                csvWriter.flush();
                                zipOut.closeEntry();
                                ZipEntry synchroDataEntry = new ZipEntry(SYNC_DATA_FILE_NAME);
                                zipOut.putNextEntry(synchroDataEntry);

                                long lastUpdate = preferences.getLong(Defaults.LAST_SYNC_PREFERENCE, 0);

                                if(lastUpdate > 0) {
                                    DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.ROOT);
                                    dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

                                    printWriter.write("last successful synchronisation (UTC): " + dateFormat.format(lastUpdate));
                                } else {
                                    printWriter.write("last successful synchronisation (UTC): never");
                                }

                                printWriter.close();
                                csvWriter.close();
                                //zipOut is actually closed automatically, but stated here explicitly for convenience
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