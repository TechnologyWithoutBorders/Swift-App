package ngo.teog.swift.gui.userInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

/**
 * Shows all available information about a user and provides methods of contacting him/her.
 * @author nitelow
 */
public class UserInfoActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        TextView nameView = findViewById(R.id.nameView);
        TextView phoneView = findViewById(R.id.phoneView);
        TextView mailView = findViewById(R.id.mailView);
        TextView positionView = findViewById(R.id.positionView);
        TextView hospitalView = findViewById(R.id.hospitalView);

        Intent intent = this.getIntent();
        int userId = intent.getIntExtra(ResourceKeys.USER_ID, -1);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        UserInfoViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(UserInfoViewModel.class);
        viewModel.init(userId);
        viewModel.getUserInfo().observe(this, userInfo -> {
            if(userInfo != null) {
                this.user = userInfo.getUser();

                nameView.setText(user.getName());
                phoneView.setText(user.getPhone());
                mailView.setText(user.getMail());
                positionView.setText(user.getPosition());
                hospitalView.setText(userInfo.getHospitals().get(0).getName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share) {
            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            Intent intent = new Intent(Intent.ACTION_SEND);

            String assetString = getString(R.string.user).toLowerCase();
            String sharingString = String.format(getString(R.string.want_to_show), assetString, Defaults.HOST, assetString, preferences.getString(Defaults.COUNTRY_PREFERENCE, null), user.getHospital());
            intent.putExtra(Intent.EXTRA_TEXT,sharingString + user.getId());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share_link)));
            return true;
        }

        return super.onOptionsItemSelected(item, R.string.userinfo_activity);//TODO Tutorial
    }

    public void invokeCall(View view) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(Defaults.URI_TEL_PREFIX + user.getPhone()));
        startActivity(dialIntent);
    }

    public void invokeMail(View view) {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(Defaults.URI_MAILTO_PREFIX + user.getMail());
        mailIntent.setData(data);
        startActivity(mailIntent);
    }
}



