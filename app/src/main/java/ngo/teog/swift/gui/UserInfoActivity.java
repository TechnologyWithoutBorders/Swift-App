package ngo.teog.swift.gui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.User;

public class UserInfoActivity extends BaseActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");

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

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(user.getFullName());

        TextView phoneView = findViewById(R.id.phoneView);
        phoneView.setText(user.getPhone());

        TextView mailView = findViewById(R.id.mailView);
        mailView.setText(user.getMail());

        TextView hospitalView = findViewById(R.id.hospitalView);
        hospitalView.setText(user.getHospital().getName());

        TextView positionView = findViewById(R.id.positionView);
        positionView.setText(user.getPosition());
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
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT,"I want to show you this user: http://teog.virlep.de/user/" + Integer.toString(user.getID()));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share user link"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void invokeCall(View view) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + user.getPhone()));
        startActivity(dialIntent);
    }

    public void invokeMail(View view) {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + user.getMail());
        mailIntent.setData(data);
        startActivity(mailIntent);
    }
}
