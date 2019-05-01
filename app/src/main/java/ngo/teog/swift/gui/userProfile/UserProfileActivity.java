package ngo.teog.swift.gui.userProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Date;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class UserProfileActivity extends BaseActivity {

    private TextView telephoneView;
    private TextView mailView;
    private TextView hospitalView;
    private TextView positionView;
    private TextView nameView;

    private ImageView imageView;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameView = findViewById(R.id.nameView);

        telephoneView = findViewById(R.id.phoneView);
        mailView = findViewById(R.id.mailView);
        hospitalView = findViewById(R.id.locationView);
        positionView = findViewById(R.id.positionView);

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        imageView = findViewById(R.id.imageView);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int id = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel.class);
        viewModel.init(id);
        viewModel.getUser().observe(this, user -> {
            if(user != null) {
                nameView.setText(user.getName());
                telephoneView.setText(user.getPhone());
                mailView.setText(user.getMail());
                positionView.setText(user.getPosition());
                hospitalView.setText(Integer.toString(user.getHospital()));
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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.userprofile_activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void editPhone(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Telephone");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(telephoneView.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                telephoneView.setText(input.getText().toString());
                save();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void editMail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mail Address");
        builder.setMessage("You will need to remember your new mail address in order to log in.");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(mailView.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mailView.setText(input.getText().toString());
                save();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void editPosition(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Position");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(positionView.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positionView.setText(input.getText().toString());
                save();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void save() {
        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        User user = new User(preferences.getInt(Defaults.ID_PREFERENCE, -1), telephoneView.getText().toString(), mailView.getText().toString(), nameView.getText().toString(), viewModel.getUser().getValue().getHospital(), positionView.getText().toString(), new Date().getTime()/1000);

        viewModel.updateUser(user);
    }
}
