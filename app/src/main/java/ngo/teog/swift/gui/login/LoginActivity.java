package ngo.teog.swift.gui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.helpers.Defaults;

public class LoginActivity extends BaseActivity {

    private EditText mailField;
    private EditText passwordField;
    private LinearLayout form;
    private ImageView imageView;
    private Spinner countrySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_login);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView versionView = findViewById(R.id.version_view);
            versionView.setText("v" + version);
        } catch(PackageManager.NameNotFoundException e) {
            //ignore
        }

        imageView = findViewById(R.id.imageView2);

        imageView.setBackgroundResource(R.drawable.logo_layer);

        form = findViewById(R.id.form);

        mailField = findViewById(R.id.mailText);
        passwordField = findViewById(R.id.pwText);

        countrySpinner = findViewById(R.id.countrySpinner);

        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        if(preferences.contains(Defaults.ID_PREFERENCE) && preferences.contains(Defaults.PW_PREFERENCE)) {
            form.setVisibility(View.GONE);

            RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);

            imageView.startAnimation(anim);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            //finish activity, so it is removed from the stack
            LoginActivity.this.finish();
        } else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.countries_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countrySpinner.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.about_text);
                return true;
            case R.id.stats:
                showStats();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the statistics activity.
     */
    private void showStats() {
        Intent intent = new Intent(LoginActivity.this, StatsActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        if(mailField.getText().length() > 0) {
            if(passwordField.getText().length() > 0) {
                if(checkForInternetConnection()) {
                    AnimationDrawable anim = (AnimationDrawable)imageView.getBackground();

                    RequestFactory.LoginRequest request = RequestFactory.getInstance().createLoginRequest(this, anim, form, mailField.getText().toString(), getSHA256Hash(passwordField.getText().toString()), (String)countrySpinner.getSelectedItem());

                    form.setVisibility(View.GONE);

                    imageView.setImageDrawable(null);
                    anim.start();

                    VolleyManager.getInstance(this).getRequestQueue().add(request);
                } else {
                    Toast.makeText(this.getApplicationContext(), getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                passwordField.setError(getText(R.string.error_empty_password));
            }
        } else {
            mailField.setError(getText(R.string.error_empty_mail));
        }
    }

    public void resetPassword(View view) {
        String country = (String)countrySpinner.getSelectedItem();

        if(mailField.getText().length() > 0) {
            String mail = mailField.getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Reset password");
            builder.setMessage("Do you want to reset your password?\nA new one will be sent to:\n" + mail);

            DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> {
                if(checkForInternetConnection()) {
                    RequestFactory.PasswordResetRequest request = RequestFactory.getInstance().createPasswordResetRequest(this, mail, country);

                    VolleyManager.getInstance(this).getRequestQueue().add(request);
                } else {
                    Toast.makeText(this.getApplicationContext(), getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            };

            DialogInterface.OnClickListener negativeListener = (dialogInterface, i) -> dialogInterface.cancel();

            builder.setPositiveButton(getText(R.string.dialog_ok_text), positiveListener);
            builder.setNegativeButton(getText(R.string.dialog_cancel_text), negativeListener);

            builder.show();
        } else {
            mailField.setError("please enter your e-mail address first");
        }
    }

    /**
     * Returns a SHA-256 hash of the given password.
     * @param password password that should be hashed
     * @return SHA-256 hash
     */
    private String getSHA256Hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.reset();

            byte[] result = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(byte b :  result) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e1) {
            return null;
        }
    }
}
