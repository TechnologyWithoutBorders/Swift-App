package ngo.teog.swift.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.Hospital;
import ngo.teog.swift.helpers.Response;
import ngo.teog.swift.helpers.ResponseException;
import ngo.teog.swift.helpers.ResponseParser;
import ngo.teog.swift.helpers.User;
import ngo.teog.swift.helpers.filters.UserFilter;

public class UserProfileActivity extends AppCompatActivity {

    private TextView telephoneView;
    private TextView mailView;
    private TextView hospitalView;
    private TextView positionView;
    private TextView nameView;

    private ProgressBar progressBar2;
    private TableLayout tableLayout;

    private ImageView imageView;

    private Button saveButton;
    private ProgressBar saveProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        nameView = findViewById(R.id.nameView);

        telephoneView = findViewById(R.id.phoneView);
        mailView = findViewById(R.id.mailView);
        hospitalView = findViewById(R.id.locationView);
        positionView = findViewById(R.id.positionView);

        progressBar2 = findViewById(R.id.progressBar2);

        tableLayout = findViewById(R.id.tableLayout);
        imageView = findViewById(R.id.imageView);

        saveButton = findViewById(R.id.saveButton);
        saveProgressBar = findViewById(R.id.saveProgressBar);

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            ProfileOpenRequest request = createProfileOpenRequest(this, progressBar2, tableLayout);

            progressBar2.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);

            queue.add(request);
        }
    }

    public void editPhone(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Telephone");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                telephoneView.setText(input.getText().toString());
                saveButton.setEnabled(true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void save(View view) {
        if(checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            SharedPreferences preferences = this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            RequestFactory.DefaultRequest request = new RequestFactory().createProfileUpdateRequest(this, saveProgressBar, saveButton, preferences.getInt(Defaults.ID_PREFERENCE, -1), telephoneView.getText().toString());

            saveProgressBar.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);

            queue.add(request);
        }
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

    public ProfileOpenRequest createProfileOpenRequest(Context context, View disable, View enable) {
        String url = ProfileOpenRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "fetch_user");
        params.put("validation_id", Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
        params.put("validation_pw", preferences.getString(Defaults.PW_PREFERENCE, null));
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));

        JSONObject request = new JSONObject(params);

        return new ProfileOpenRequest(context, disable, enable, url, request);
    }

    public class ProfileOpenRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/users.php";

        public ProfileOpenRequest(final Context context, final View disable, final View enable, final String url, JSONObject request) {
            super(Request.Method.POST, url, request, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<User> userList = new ResponseParser().parseUserList(response);
                        User user = userList.get(0);

                        nameView.setText(user.getFullName());
                        telephoneView.setText(user.getPhone());
                        mailView.setText(user.getMail());
                        hospitalView.setText(user.getHospital().getName());
                        positionView.setText(user.getPosition());

                        /*if(userObject.has("picture")) {
                            String imageData = userObject.getString("picture");

                            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            imageView.setImageBitmap(bitmap);
                        }*/
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
