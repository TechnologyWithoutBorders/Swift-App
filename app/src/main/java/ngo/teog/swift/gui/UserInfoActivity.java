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

        TextView mailView = findViewById(R.id.mailView);
        mailView.setText(user.getMail());

        if (this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            RequestFactory.UserImageRequest request = new RequestFactory().createUserImageRequest(this, progressBar, globalImageView, user.getID());

            progressBar.setVisibility(View.VISIBLE);
            globalImageView.setVisibility(View.INVISIBLE);

            queue.add(request);
        }
    }

    public void invokeCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + user.getPhone()));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);
        } else {
            startActivity(callIntent);//TODO lieber in Callback packen
        }
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
