package ngo.teog.swift.gui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import org.w3c.dom.Text;

import java.io.InputStream;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.User;

public class UserInfoActivity extends AppCompatActivity {

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

        /*if (this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.DefaultRequest request = new RequestFactory().createUserImageRequest(this, progressBar, globalImageView, user.getID());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.INVISIBLE);

            queue.add(request);
        }*/
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

    private boolean checkForInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
