package ngo.teog.swift.gui.userInfo;

import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class UserInfoActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private UserInfoViewModel viewModel;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = this.getIntent();
        int userId = intent.getIntExtra(Defaults.USER_ID_KEY, -1);

        final ImageView globalImageView = findViewById(R.id.imageView);
        globalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog imageDialog = new Dialog(UserInfoActivity.this);
                imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                View dialogView = getLayoutInflater().inflate(R.layout.image_dialog, null);
                ImageView imageView = dialogView.findViewById(R.id.imageView);
                imageView.setImageBitmap(((BitmapDrawable) globalImageView.getDrawable()).getBitmap());
                imageDialog.setContentView(dialogView);
                imageDialog.show();
            }
        });

        TextView nameView = findViewById(R.id.nameView);
        TextView phoneView = findViewById(R.id.phoneView);
        TextView mailView = findViewById(R.id.mailView);
        TextView positionView = findViewById(R.id.positionView);
        TextView hospitalView = findViewById(R.id.hospitalView);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
        int myId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserInfoViewModel.class);
        viewModel.init(myId, userId);
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
        // Handle item selection
        switch(item.getItemId()) {
            case R.id.info:
                showInfo(R.string.userinfo_activity);
                return true;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this user: http://teog.virlep.de/user/" + Integer.toString(user.getId()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share user link"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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



