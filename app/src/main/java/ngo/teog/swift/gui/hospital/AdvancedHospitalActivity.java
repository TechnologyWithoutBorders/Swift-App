package ngo.teog.swift.gui.hospital;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.BaseResponseListener;
import ngo.teog.swift.communication.BaseRequest;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.UserAction;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.AppModule;
import ngo.teog.swift.helpers.data.DaggerAppComponent;
import ngo.teog.swift.helpers.data.RoomModule;
import ngo.teog.swift.helpers.data.ViewModelFactory;

public class AdvancedHospitalActivity extends BaseActivity {

    private EditText nameText, mailText;
    private Button createButton;
    private ProgressBar progressBar;

    @Inject
    ViewModelFactory viewModelFactory;

    private AdvancedHospitalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_hospital);

        nameText = findViewById(R.id.nameText);
        mailText = findViewById(R.id.mailText);
        createButton = findViewById(R.id.create_button);
        progressBar = findViewById(R.id.progressBar);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(AdvancedHospitalViewModel.class);
    }

    public BaseRequest createUserCreationRequest(final Context context, View disable, final View enable, String userName, String userMail) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = RequestFactory.generateParameterMap(context, UserAction.CREATE_USER, true);
        params.put(ResourceKeys.USER_NAME, userName);
        params.put(ResourceKeys.USER_MAIL, userMail);

        JSONObject request = new JSONObject(params);

        return new BaseRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                super.onSuccess(response);

                Toast.makeText(context.getApplicationContext(), getString(R.string.user_created), Toast.LENGTH_LONG).show();

                nameText.setText(null);
                mailText.setText(null);

                SharedPreferences preferences = AdvancedHospitalActivity.this.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                int userId = preferences.getInt(Defaults.ID_PREFERENCE, -1);

                viewModel.refreshHospital(userId);
            }
        });
    }

    public void createUser(View view) {
        String name = nameText.getText().toString().trim();
        String mail = mailText.getText().toString().trim();

        if(name.length() > 0) {
            if(mail.length() > 0) {
                if (this.checkForInternetConnection()) {
                    RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

                    BaseRequest request = this.createUserCreationRequest(this, progressBar, createButton, name, mail);

                    progressBar.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.GONE);

                    queue.add(request);
                } else {
                    Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                mailText.setError(getString(R.string.error_empty_mail));
            }
        } else {
            nameText.setError(getString(R.string.empty_name));
        }
    }
}
