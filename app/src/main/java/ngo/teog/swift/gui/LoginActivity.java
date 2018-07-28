package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.gui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText mailField;
    private EditText passwordField;
    private LinearLayout form;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_login);

        imageView = findViewById(R.id.imageView2);

        imageView.setBackgroundResource(R.drawable.logo_layer);

        form = findViewById(R.id.form);

        loginButton = findViewById(R.id.loginButton);
        mailField = findViewById(R.id.mailText);
        passwordField = findViewById(R.id.pwText);

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

            //finishen nicht vergessen, damit die Activity aus dem Stack entfernt wird
            LoginActivity.this.finish();
        } else {
            //TODO
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    public void login(View view) {
        if(mailField.getText().length() > 0) {
            if(passwordField.getText().length() > 0) {
                if(checkForInternetConnection()) {
                    AnimationDrawable anim = (AnimationDrawable)imageView.getBackground();

                    RequestFactory.LoginRequest request = new RequestFactory().createLoginRequest(this, anim, form, mailField.getText().toString(), getHash(passwordField.getText().toString()));

                    form.setVisibility(View.GONE);

                    imageView.setImageDrawable(null);
                    anim.start();

                    VolleyManager.getInstance(this).getRequestQueue().add(request);
                } else {
                    Toast.makeText(this.getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                passwordField.setError("empty password");
            }
        } else {
            mailField.setError("empty mail address");
        }
    }

    /**
     * Mit dieser Methode wird ein übergebenes Passwort SHA-256-verschlüsselt
     * @param password zu verschlüsselndes Passwort
     * @return Hash
     */
    private String getHash(String password) {
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

    protected boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }
}
