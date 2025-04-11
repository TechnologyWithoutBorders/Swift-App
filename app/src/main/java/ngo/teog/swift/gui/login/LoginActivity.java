package ngo.teog.swift.gui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.BaseErrorListener;
import ngo.teog.swift.communication.BaseResponseListener;
import ngo.teog.swift.communication.DataAction;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.ResponseParser;
import ngo.teog.swift.communication.ServerException;
import ngo.teog.swift.communication.SwiftResponse;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.filters.HospitalAttribute;
import ngo.teog.swift.helpers.filters.UserAttribute;

/**
 * Provides login functionality
 * @author nitelow
 */
public class LoginActivity extends BaseActivity {

    private EditText mailField, passwordField;
    private LinearLayout form;
    private ImageView imageView;
    private AnimationDrawable anim;
    private Spinner countrySpinner, hospitalSpinner;

    private AppUpdateManager appUpdateManager;

    private static final int MAX_STALENESS_DAYS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionString = "v"  + pInfo.versionName + "-" + pInfo.versionCode;
            TextView versionView = findViewById(R.id.version_view);
            versionView.setText(versionString);
        } catch(PackageManager.NameNotFoundException e) {
            //ignore
        }

        imageView = findViewById(R.id.imageView2);
        imageView.setBackgroundResource(R.drawable.logo_layer);
        anim = (AnimationDrawable)imageView.getBackground();

        form = findViewById(R.id.form);

        mailField = findViewById(R.id.mailText);
        passwordField = findViewById(R.id.pwText);

        countrySpinner = findViewById(R.id.countrySpinner);
        hospitalSpinner = findViewById(R.id.hospitalSpinner);

        ArrayAdapter<Hospital> adapter = new HospitalAdapter(LoginActivity.this, new ArrayList<>());
        hospitalSpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.clear();

                if(checkForInternetConnection()) {
                    String country = parent.getItemAtPosition(position).toString();

                    Map<String, String> params = RequestFactory.generateParameterMap(LoginActivity.this, DataAction.GET_HOSPITALS, false);
                    params.put(ResourceKeys.COUNTRY, country);

                    JSONObject jsonRequest = new JSONObject(params);

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,//TODO: use get
                            Defaults.BASE_URL + Defaults.INFO_URL,
                            jsonRequest,
                            new BaseResponseListener(LoginActivity.this) {
                                @Override
                                public void onSuccess(JSONObject response) throws JSONException, ParseException {
                                    JSONArray hospitalsArray = response.getJSONArray(SwiftResponse.DATA_FIELD);
                                    List<Hospital> hospitals = new ArrayList<>();

                                    DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.getDefault());
                                    dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

                                    for (int i = 0; i < hospitalsArray.length(); i++) {
                                        JSONObject hospitalObject = hospitalsArray.getJSONObject(i);

                                        int hospitalId = hospitalObject.getInt(HospitalAttribute.ID);
                                        String name = hospitalObject.getString(HospitalAttribute.NAME);
                                        String hospitalLocation = hospitalObject.getString(HospitalAttribute.LOCATION);
                                        float longitude = Float.parseFloat(hospitalObject.getString(HospitalAttribute.LONGITUDE));
                                        float latitude = Float.parseFloat(hospitalObject.getString(HospitalAttribute.LATITUDE));
                                        Date hospitalLastUpdate = dateFormat.parse(hospitalObject.getString(HospitalAttribute.LAST_UPDATE));

                                        hospitals.add(new Hospital(hospitalId, name, hospitalLocation, longitude, latitude, hospitalLastUpdate));
                                    }

                                    adapter.addAll(hospitals);
                                }
                            },
                            new BaseErrorListener(LoginActivity.this)
                    );

                    VolleyManager.getInstance(LoginActivity.this).getRequestQueue().add(request);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //ignore
            }
        });

        if(checkForInternetConnection()) {
            setLoadingScreen();

            ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if(result.getResultCode() != RESULT_OK) {
                            Toast.makeText(this, "update failed", Toast.LENGTH_SHORT).show();
                        }
                    });


            //Check whether a new app version is available
            appUpdateManager = AppUpdateManagerFactory.create(this);
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    Integer stalenessDays = appUpdateInfo.clientVersionStalenessDays();

                    if(stalenessDays != null) {
                        if(stalenessDays >= MAX_STALENESS_DAYS && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
                        }
                    }
                }

                initLoginOptions();
            });
        } else {
            initLoginOptions();
        }
    }

    private void setLoadingScreen() {
        form.setVisibility(View.GONE);

        imageView.setImageDrawable(null);
        anim.start();
    }

    private void setForm() {
        anim.stop();

        imageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_applogo_ohne_kontur_rot1));
        form.setVisibility(View.VISIBLE);
    }

    private void initLoginOptions() {
        SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        setLoadingScreen();

        if(preferences.contains(Defaults.ID_PREFERENCE) && preferences.contains(Defaults.PW_PREFERENCE)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            //finish activity, so it is removed from the stack
            LoginActivity.this.finish();
        } else {
            if(checkForInternetConnection()) {
                Map<String, String> params = RequestFactory.generateParameterMap(this, DataAction.GET_COUNTRIES, false);
                JSONObject jsonRequest = new JSONObject(params);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,//TODO: use get
                        Defaults.BASE_URL + Defaults.INFO_URL,
                        jsonRequest,
                        new BaseResponseListener(this) {
                            @Override
                            public void onSuccess(JSONObject response) throws JSONException {
                                JSONArray countriesArray = response.getJSONArray(SwiftResponse.DATA_FIELD);
                                String[] countries = new String[countriesArray.length()];

                                for (int i = 0; i < countriesArray.length(); i++) {
                                    countries[i] = countriesArray.getString(i);
                                }

                                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, countries);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                countrySpinner.setAdapter(adapter);

                                setForm();
                            }

                            @Override
                            public void onError(Exception e) {
                                super.onError(e);

                                setForm();
                            }
                        },
                        new BaseErrorListener(this) {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                super.onErrorResponse(error);

                                setForm();
                            }
                        }
                );

                VolleyManager.getInstance(this).getRequestQueue().add(request);
            } else {
                Toast.makeText(this, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
                setForm();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: klappt das mit dem Ablauf?
        if(appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(
                                new ActivityResultContracts.StartIntentSenderForResult(),
                                result -> {
                                    if(result.getResultCode() != RESULT_OK) {
                                        Toast.makeText(this, "update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
                    }
                }
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.info) {
            showInfo(R.string.about_text, R.string.privacy_policy);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        String mailAddress = mailField.getText().toString();
        String password = passwordField.getText().toString();
        String country = (String)countrySpinner.getSelectedItem();
        Object hospital = hospitalSpinner.getSelectedItem();

        if(!mailAddress.isEmpty()) {
            if(!password.isEmpty()) {
                if(country != null && hospital != null) {
                    if (checkForInternetConnection()) {
                        String hashedPassword = getSHA256Hash(password);

                        Map<String, String> params = RequestFactory.generateParameterMap(this, DataAction.LOGIN_USER, false);
                        params.put(UserAttribute.MAIL, mailAddress);
                        params.put(UserAttribute.PASSWORD, hashedPassword);
                        //Override country, because the shared preferences contain no country at this point
                        params.put(Defaults.COUNTRY_KEY, country);
                        params.put(Defaults.HOSPITAL_KEY, Integer.toString(((Hospital)hospital).getId()));

                        JSONObject jsonRequest = new JSONObject(params);

                        JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            Defaults.BASE_URL + Defaults.USERS_URL,
                            jsonRequest,
                            new BaseResponseListener(LoginActivity.this) {
                                @Override
                                public void onSuccess(JSONObject response) throws ServerException {
                                    int id = ResponseParser.parseLoginResponse(response);

                                    SharedPreferences preferences = getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt(Defaults.ID_PREFERENCE, id);
                                    editor.putString(Defaults.PW_PREFERENCE, hashedPassword);
                                    editor.putString(Defaults.COUNTRY_PREFERENCE, country);
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);

                                    //remove activity from stack
                                    LoginActivity.this.finish();
                                }

                                @Override
                                public void onError(Exception e) {
                                    super.onError(e);

                                    setForm();
                                }
                            },
                            new BaseErrorListener(LoginActivity.this) {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    super.onErrorResponse(error);

                                    setForm();
                                }
                            }
                        );

                        setLoadingScreen();

                        Log.v(this.getClass().getName(), "trying to log in user with mail address " + mailAddress);
                        VolleyManager.getInstance(this).getRequestQueue().add(request);
                    } else {
                        Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "please select your hospital", Toast.LENGTH_SHORT).show();
                }
            } else {
                passwordField.setError(getText(R.string.error_empty_password));
            }
        } else {
            mailField.setError(getText(R.string.error_empty_mail));
        }
    }

    public void resetPassword(View view) {
        String mailAddress = mailField.getText().toString();

        if(!mailAddress.isEmpty()) {
            String country = (String)countrySpinner.getSelectedItem();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.reset_pw));
            builder.setMessage(getString(R.string.reset_pw_hint) + mailAddress);

            DialogInterface.OnClickListener positiveListener = (dialogInterface, i) -> {
                if(checkForInternetConnection()) {
                    JsonObjectRequest request = RequestFactory.getInstance().createPasswordResetRequest(this, mailAddress, country);

                    VolleyManager.getInstance(this).getRequestQueue().add(request);
                } else {
                    Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            };

            DialogInterface.OnClickListener negativeListener = (dialogInterface, i) -> dialogInterface.cancel();

            builder.setPositiveButton(getText(R.string.dialog_ok_text), positiveListener);
            builder.setNegativeButton(getText(R.string.dialog_cancel_text), negativeListener);

            builder.show();
        } else {
            mailField.setError(getString(R.string.reset_pw_mail_missing));
        }
    }

    /**
     * Returns a SHA-256 hash of the given password.
     * @param password password that should be hashed
     * @return SHA-256 hash
     */
    private String getSHA256Hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.reset();

            byte[] result = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(byte b :  result) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e1) {
            return null;
        }
    }

    public static class HospitalAdapter extends ArrayAdapter<Hospital> {

        public HospitalAdapter(Context context, List<Hospital> hospitals) {
            super(context, -1, hospitals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            TextView text = convertView.findViewById(android.R.id.text1);
            text.setText(getItem(position).getLocation());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_hospital, parent, false);
            }

            TextView nameView = convertView.findViewById(R.id.name_view);
            nameView.setText(getItem(position).getName());

            TextView locationView = convertView.findViewById(R.id.location_view);
            locationView.setText(getItem(position).getLocation());

            return convertView;
        }
    }
}
