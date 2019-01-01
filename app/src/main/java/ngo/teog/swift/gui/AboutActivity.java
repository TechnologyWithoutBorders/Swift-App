package ngo.teog.swift.gui;

import android.os.Bundle;
import android.widget.TextView;

import ngo.teog.swift.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(R.string.about_text);
    }
}
