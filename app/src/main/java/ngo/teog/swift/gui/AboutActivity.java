package ngo.teog.swift.gui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

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

        String versionString = "";

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionString = String.format("%s %s v%s-%s", getString(R.string.organization_name), getString(R.string.app_name), pInfo.versionName, pInfo.versionCode);
        } catch(PackageManager.NameNotFoundException e) {
            //ignore
        }

        TextView teogInfo = findViewById(R.id.teog_info);
        teogInfo.setText(HtmlCompat.fromHtml(versionString + getString(R.string.teog_info), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(HtmlCompat.fromHtml(getString(R.string.about_text), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView privacyPolicyView = findViewById(R.id.privacy_policy_view);
        privacyPolicyView.setText(HtmlCompat.fromHtml(getString(R.string.privacy_policy), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }
}
