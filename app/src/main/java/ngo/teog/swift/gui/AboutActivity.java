package ngo.teog.swift.gui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import ngo.teog.swift.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(Html.fromHtml(getString(R.string.about_text)));

        TextView teogInfo = findViewById(R.id.teog_info);
        teogInfo.setText(Html.fromHtml(getString(R.string.teog_info)));
    }
}
