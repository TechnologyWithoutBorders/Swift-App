package ngo.teog.swift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.swift.comm.RequestFactory;
import ngo.teog.swift.comm.VolleyManager;
import ngo.teog.swift.helpers.Defaults;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button loginButton;
    private EditText mailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);
        mailField = findViewById(R.id.mailText);
        passwordField = findViewById(R.id.pwText);

        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        if(preferences.contains(getString(R.string.id_pref)) && preferences.contains(getString(R.string.pw_pref))) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            //finishen nicht vergessen, damit die Activity aus dem Stack entfernt wird
            LoginActivity.this.finish();
        } else {

        }
    }

    public void login(View view) {
        RequestFactory.LoginRequest request = new RequestFactory().createLoginRequest(this, progressBar, loginButton, mailField.getText().toString(), getHash(passwordField.getText().toString()));

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        VolleyManager.getInstance(this).getRequestQueue().add(request);
    }

    /**
     * Mit dieser Methode wird ein übergebenes Passwort SHA-256-verschlüsselt
     * @param password zu verschlüsselndes Passwort
     * @return Hash
     */
    private String getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        digest.reset();

        byte[] result = digest.digest(password.getBytes());

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
