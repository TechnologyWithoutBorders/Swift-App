package ngo.teog.hstest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.hstest.helpers.Defaults;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);

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
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.id_pref), -1);
        editor.putString(getString(R.string.pw_pref), "dummyPW");
        editor.commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        //finishen nicht vergessen, damit die Activity aus dem Stack entfernt wird
        LoginActivity.this.finish();
    }

    /**
     * Mit dieser Methode wird ein übergebenes Passwort SHA-256-verschlüsselt
     * @param password zu verschlüsselndes Passwort
     * @return Hash
     */
    private byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }
}
