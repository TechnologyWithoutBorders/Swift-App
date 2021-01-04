package ngo.teog.swift.gui.userProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Shows a user's own profile and provides ways of editing personal information.
 * @author nitelow
 */
public class UserProfileActivity extends BaseActivity {

    private TableLayout tableLayout;
    private ImageView editView;
    private TextView telephoneView, mailView, hospitalView, positionView, nameView;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tableLayout = findViewById(R.id.tableLayout);
        editView = findViewById(R.id.edit_view);

        nameView = findViewById(R.id.nameView);

        telephoneView = findViewById(R.id.phoneView);
        mailView = findViewById(R.id.mailView);
        hospitalView = findViewById(R.id.locationView);
        positionView = findViewById(R.id.positionView);

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
                Hospital hospital = userProfile.getHospitals().get(0);

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
                .add(buildTutorialStep(tableLayout, getString(R.string.user_profile_tutorial_attribute_table)))
                .add(buildTutorialStep(editView, getString(R.string.user_profile_tutorial_edit)));

            tutorialQueue.show();

            return true;
        } else {
            return super.onOptionsItemSelected(item, R.string.userprofile_activity);
        }
    }

    public void editPhone(View view) {
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

    public void editMail(View view) {
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

    public void editPosition(View view) {
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

    public void save() {
        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        User user = new User(preferences.getInt(Defaults.ID_PREFERENCE, -1), telephoneView.getText().toString().trim(), mailView.getText().toString().trim(), nameView.getText().toString().trim(), viewModel.getUserProfile().getValue().getUser().getHospital(), positionView.getText().toString().trim(), new Date());//TODO getUser.getValue schlecht

        viewModel.updateUser(user);
    }
}
