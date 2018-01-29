package ngo.teog.swift;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = (TextView)findViewById(R.id.aboutText);
        aboutText.setText("Was hier her soll:\n" +
                "- Infos zu den AGB, zum Datenschutz usw.\n" +
                "außerdem Infos zum Verein inklusive Kontaktmöglichkeiten und Spendenkonto :-)");
    }
}
