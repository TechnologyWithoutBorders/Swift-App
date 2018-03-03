package ngo.teog.swift;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import ngo.teog.swift.comm.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.Response;
import ngo.teog.swift.helpers.ResponseException;
import ngo.teog.swift.helpers.UserFilter;

public class UserProfileActivity extends AppCompatActivity {

    private TextView telephoneView;
    private TextView mailView;
    private TextView hospitalView;
    private TextView positionView;
    private TextView qualificationsView;
    private TextView nameView;

    private ProgressBar progressBar2;
    private TableLayout tableLayout;

    private ImageView imageView;

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
        qualificationsView = findViewById(R.id.qualificationsView);

        progressBar2 = findViewById(R.id.progressBar2);

        tableLayout = findViewById(R.id.tableLayout);
        imageView = findViewById(R.id.imageView);

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            ProfileOpenRequest request = createProfileOpenRequest(this, progressBar2, tableLayout);

            progressBar2.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);

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
        params.put("action", "profile");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(Defaults.PW_PREFERENCE, null));

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
                        int responseCode = response.getInt("response_code");
                        switch(responseCode) {
                            case Response.CODE_OK:
                                JSONObject userObject = response.getJSONObject("data");

                                String phone = userObject.getString(UserFilter.PHONE);
                                String mail = userObject.getString(UserFilter.MAIL);
                                String fullName = userObject.getString(UserFilter.FULL_NAME);
                                String qualifications = userObject.getString(UserFilter.QUALIFICATIONS);

                                nameView.setText(fullName);
                                telephoneView.setText(phone);
                                mailView.setText(mail);
                                qualificationsView.setText(qualifications);

                                if(userObject.has("picture")) {
                                    String imageData = userObject.getString("picture");

                                    byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    imageView.setImageBitmap(bitmap);
                                }

                                break;
                            case Response.CODE_FAILED_VISIBLE:
                                throw new ResponseException(response.getString("data"));
                            case Response.CODE_FAILED_HIDDEN:
                            default:
                                throw new Exception(response.getString("data"));
                        }
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
