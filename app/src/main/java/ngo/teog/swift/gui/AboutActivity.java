package ngo.teog.swift.gui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import ngo.teog.swift.R;

/**
 * Activity that shows some general information about the application.
 * @author nitelow
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionString = "TeoG Swift v" + pInfo.versionName;
            TextView versionView = findViewById(R.id.version_view);
            versionView.setText(versionString);
        } catch(PackageManager.NameNotFoundException e) {
            //ignore
        }

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(Html.fromHtml(getString(R.string.about_text)));

        TextView teogInfo = findViewById(R.id.teog_info);
        teogInfo.setText(Html.fromHtml(getString(R.string.teog_info)));

        TextView privacyPolicyView = findViewById(R.id.privacy_policy_view);
        privacyPolicyView.setText(Html.fromHtml(getString(R.string.privacy_policy)));
    }
}
