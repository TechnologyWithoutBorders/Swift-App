package ngo.teog.swift.gui.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.reportCreation.ReportCreationActivity;
import ngo.teog.swift.gui.reportCreation.ReportCreationViewModel;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class AdvancedHospitalActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText mailText;

    @Inject
    ViewModelFactory viewModelFactory;

    private AdvancedHospitalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_hospital);

        nameText = findViewById(R.id.nameText);
        mailText = findViewById(R.id.mailText);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AdvancedHospitalViewModel.class);
    }

    public void createUser(View view) {
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        String name = nameText.getText().toString();
        String mail = mailText.getText().toString();

        //User user = new User(-1, null, mail, name, hospital, null, new Date());//TODO

        //viewModel.createUser(user, preferences.getInt(Defaults.ID_PREFERENCE, -1));

        Toast.makeText(this.getApplicationContext(), "user created", Toast.LENGTH_LONG).show();
    }
}
