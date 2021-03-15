package ngo.teog.swift.gui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

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
        teogInfo.setText(Html.fromHtml(versionString + getString(R.string.teog_info)));

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText(Html.fromHtml(getString(R.string.about_text)));

        TextView privacyPolicyView = findViewById(R.id.privacy_policy_view);
        privacyPolicyView.setText(Html.fromHtml(getString(R.string.privacy_policy)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.licenseItem) {
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
