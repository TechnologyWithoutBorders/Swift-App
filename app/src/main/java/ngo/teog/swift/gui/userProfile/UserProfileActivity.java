package ngo.teog.swift.gui.userProfile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.opencsv.CSVWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceStateVisuals;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Shows a user's own profile and provides ways of editing personal information.
 * @author nitelow
 */
public class UserProfileActivity extends BaseActivity {

    private TableLayout tableLayout;
    private TextView telephoneView, mailView, hospitalView, positionView, nameView, editPosition;
    private EditText startDate, endDate;

    private LocalDate start, end;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        tableLayout = findViewById(R.id.tableLayout);
        TextView editName = findViewById(R.id.edit_name);
        editName.setOnClickListener((view) -> editName());
        editPosition = findViewById(R.id.edit_position);
        editPosition.setOnClickListener((view) -> editPosition());
        TextView editPhone = findViewById(R.id.edit_phone);
        editPhone.setOnClickListener((view) -> editPhone());
        TextView editMail = findViewById(R.id.edit_mail);
        editMail.setOnClickListener((view) -> editMail());

        nameView = findViewById(R.id.nameView);

        telephoneView = findViewById(R.id.phoneView);
        mailView = findViewById(R.id.mailView);
        hospitalView = findViewById(R.id.hospitalView);
        positionView = findViewById(R.id.positionView);

        start = LocalDate.now().minusWeeks(4);
        end = LocalDate.now();

        startDate = findViewById(R.id.start_date);
        startDate.setText(Defaults.dateFormatter.format(start));

        endDate = findViewById(R.id.end_date);
        endDate.setText(Defaults.dateFormatter.format(end));

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(UserProfileViewModel.class);
        viewModel.init(id);
        viewModel.getUserProfile().observe(this, userProfile -> {
            if(userProfile != null) {
                User user = userProfile.getUser();
                Hospital hospital = userProfile.getHospital();

                nameView.setText(user.getName());
                telephoneView.setText(user.getPhone());
                mailView.setText(user.getMail());
                positionView.setText(user.getPosition());
                hospitalView.setText(hospital.getName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.info) {
            //Show tutorial
            FancyShowCaseQueue tutorialQueue = new FancyShowCaseQueue()
                .add(buildTutorialStep(tableLayout, getString(R.string.user_profile_tutorial_attribute_table), Gravity.CENTER))
                .add(buildTutorialStep(editPosition, getString(R.string.user_profile_tutorial_edit), Gravity.CENTER));

            tutorialQueue.show();

            return true;
        } else {
            return super.onOptionsItemSelected(item, R.string.userprofile_activity);
        }
    }

    public void editPhone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.user_telephone));

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(telephoneView.getText());
        builder.setView(input);

        builder.setPositiveButton(getText(R.string.dialog_ok_text), (dialog, which) -> {
            telephoneView.setText(input.getText().toString());
            save();
        });
        builder.setNegativeButton(R.string.dialog_cancel_text, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void editMail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.user_mail));
        builder.setMessage(getString(R.string.edit_mail_address_warning));

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(mailView.getText());
        builder.setView(input);

        builder.setPositiveButton(getText(R.string.dialog_ok_text), (dialog, which) -> {
            mailView.setText(input.getText().toString());
            save();
        });
        builder.setNegativeButton(getText(R.string.dialog_cancel_text), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void editPosition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.user_position));

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(positionView.getText());
        builder.setView(input);

        builder.setPositiveButton(getText(R.string.dialog_ok_text), (dialog, which) -> {
            positionView.setText(input.getText().toString());
            save();
        });
        builder.setNegativeButton(getText(R.string.dialog_cancel_text), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void editName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.user_name));

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(nameView.getText());
        builder.setView(input);

        builder.setPositiveButton(getText(R.string.dialog_ok_text), (dialog, which) -> {
            nameView.setText(input.getText().toString());
            save();
        });
        builder.setNegativeButton(getText(R.string.dialog_cancel_text), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void save() {
        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        User user = new User(preferences.getInt(Defaults.ID_PREFERENCE, -1), telephoneView.getText().toString().trim(), mailView.getText().toString().trim(), nameView.getText().toString().trim(), viewModel.getUserProfile().getValue().getUser().getHospital(), positionView.getText().toString().trim(), true, new Date());//TODO getUser.getValue schlecht

        viewModel.updateUser(user);
    }

    public void selectStartDate(View view) {
        int y = start.getYear();
        int m = start.getMonthValue()-1;
        int d = start.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view1, year, month, day) -> {
                start = LocalDate.of(year, month+1, day);
                startDate.setText(Defaults.dateFormatter.format(start));
            }, y, m, d);

        datePickerDialog.show();
    }

    public void selectEndDate(View view) {
        int y = end.getYear();
        int m = end.getMonthValue()-1;
        int d = end.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year, month, day) -> {
                    end = LocalDate.of(year, month+1, day);
                    endDate.setText(Defaults.dateFormatter.format(end));
                }, y, m, d);

        datePickerDialog.show();
    }

    public void exportCSV(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("text/csv")
                .putExtra(Intent.EXTRA_TITLE, Defaults.ACTIVITY_EXPORT_FILE_NAME);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri fileUri = data.getData();

                    SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

                    viewModel.getHospitalDevices().observe(this, deviceInfos -> {
                        if (deviceInfos != null) {
                            try {
                                //TODO streams/writers should be closed in "finally"-block
                                CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new BufferedOutputStream(getContentResolver().openOutputStream(fileUri))));

                                csvWriter.writeNext(new String[]{"Device", "Manufacturer", "Model", "Serial No.", "Author", "Created", "Title", "Current State", "Description"});

                                for (DeviceInfo deviceInfo : deviceInfos) {
                                    for (ReportInfo reportInfo : deviceInfo.getReports()) {
                                        User author = reportInfo.getAuthor();

                                        if(author.getId() == userId) {
                                            HospitalDevice device = deviceInfo.getDevice();
                                            Report report = reportInfo.getReport();

                                            LocalDate creationDate = report.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                            if((creationDate.isEqual(start) || creationDate.isAfter(start)) && (creationDate.isBefore(end) || creationDate.isEqual(end))) {
                                                DeviceStateVisuals newVisuals = new DeviceStateVisuals(report.getCurrentState(), this);

                                                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.ROOT);
                                                dateFormat.setTimeZone(TimeZone.getDefault());

                                                csvWriter.writeNext(new String[]{device.getType(), device.getManufacturer(), device.getModel(), device.getSerialNumber(), author.getName(), dateFormat.format(report.getCreated()), report.getTitle(), newVisuals.getStateString(), report.getDescription()});
                                            }
                                        }
                                    }
                                }

                                csvWriter.flush();
                                csvWriter.close();
                            } catch (IOException e) {
                                Toast.makeText(this, getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }
}
