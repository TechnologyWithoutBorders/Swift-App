package ngo.teog.swift.gui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
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

import com.android.volley.toolbox.JsonObjectRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.helpers.Defaults;

/**
 * Provides login functionality
 * @author nitelow
 */
public class LoginActivity extends BaseActivity {

    private EditText mailField, passwordField;
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
            String versionString = "v"  + pInfo.versionName;
            TextView versionView = findViewById(R.id.version_view);
            versionView.setText(versionString);
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
        if(item.getItemId() == R.id.info) {
            showInfo(R.string.about_text, R.string.privacy_policy);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        String mailAddress = mailField.getText().toString();
        String password = passwordField.getText().toString();
        String country = (String)countrySpinner.getSelectedItem();

        if(mailAddress.length() > 0) {
            if(password.length() > 0) {
                if(checkForInternetConnection()) {
                    AnimationDrawable anim = (AnimationDrawable)imageView.getBackground();

                    JsonObjectRequest request = RequestFactory.getInstance().createLoginRequest(this, anim, form, mailAddress, getSHA256Hash(password), country);

                    form.setVisibility(View.GONE);

                    imageView.setImageDrawable(null);
                    anim.start();

                    Log.v(this.getClass().getName(), "trying to log in user with mail address " + mailAddress);
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
        String mailAddress = mailField.getText().toString();

        if(mailAddress.length() > 0) {
            String country = (String)countrySpinner.getSelectedItem();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.reset_pw));
            builder.setMessage(getString(R.string.reset_pw_hint) + mailAddress);

            DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> {
                if(checkForInternetConnection()) {
                    JsonObjectRequest request = RequestFactory.getInstance().createPasswordResetRequest(this, mailAddress, country);

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
            mailField.setError(getString(R.string.reset_pw_mail_missing));
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
