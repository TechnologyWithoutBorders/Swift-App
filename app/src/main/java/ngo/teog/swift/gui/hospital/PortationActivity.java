package ngo.teog.swift.gui.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                                ZipOutputStream zipOut = new ZipOutputStream(getContentResolver().openOutputStream(fileUri));

                                CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOut));

                                ZipEntry entry = new ZipEntry("hospitals.csv");

                                zipOut.putNextEntry(entry);

                                Hospital hospital = hospitalDump.getHospital();

                                writer.writeNext(new String[]{"ID", "Name", "Location", "Longitude", "Latitude"});
                                writer.writeNext(new String[]{Integer.toString(hospital.getId()), hospital.getName(), hospital.getLocation(), Float.toString(hospital.getLongitude()), Float.toString(hospital.getLatitude())});

                                writer.close();

                                zipOut.close();
                            } catch (IOException e) {
                                //Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                                Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }
}
