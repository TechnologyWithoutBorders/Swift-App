package ngo.teog.swift.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.swift.R;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.filters.DeviceAttribute;
import ngo.teog.swift.helpers.filters.UserAttribute;

/**
 * Singleton class that creates HTTPS requests for communication with the server.
 * @author nitelow
 */
public class RequestFactory {
    private static RequestFactory instance;

    public static synchronized RequestFactory getInstance() {
        if(instance == null) {
            instance = new RequestFactory();
        }

        return instance;
    }

    // make default constructor private (singleton class!)
    private RequestFactory() {}

    public JsonObjectRequest createDeviceImageRequest(final Context context, View loadingView, final ImageView imageView, final int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DataAction.FETCH_DEVICE_IMAGE, true);
        params.put(DeviceAttribute.ID, Integer.toString(id));

        JSONObject jsonRequest = new JSONObject(params);

        return new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new BaseResponseListener(context) {

            @Override
            public  void onResponse(JSONObject response) {
                super.onResponse(response);

                loadingView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                super.onSuccess(response);

                String imageData = response.getString(SwiftResponse.DATA_FIELD);

                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                FileOutputStream outputStream;

                try {
                    File dir = new File(context.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                    boolean created = dir.mkdirs();

                    if(created) {
                        Log.v(this.getClass().getName(), "image directory has been created");
                    }

                    outputStream = new FileOutputStream(new File(dir, id + ".jpg"));
                    outputStream.write(decodedString);
                    outputStream.close();
                } catch(IOException e) {
                    Log.w(this.getClass().getName(), "writing image data failed: " + e);
                }

                imageView.setImageBitmap(bitmap);
            }
        }, new BaseErrorListener(context));
    }

    public JsonObjectRequest createPasswordResetRequest(Context context, String mail, String country) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, DataAction.RESET_PASSWORD, false);

        params.put(UserAttribute.MAIL, mail);
        //Override country, because the shared preferences contain no country at this point
        params.put(Defaults.COUNTRY_KEY, country);

        JSONObject jsonRequest = new JSONObject(params);

        return new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new BaseResponseListener(context) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                super.onSuccess(response);

                Toast.makeText(context.getApplicationContext(), context.getString(R.string.e_mail_sent), Toast.LENGTH_SHORT).show();
            }
        }, new BaseErrorListener(context));
    }

    //TODO merge with createDeviceImageRequest
    public JsonObjectRequest createImageHashRequest(Context context, int device, String hash, ImageView imageView) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DataAction.FETCH_DEVICE_IMAGE_HASH, true);

        params.put(DeviceAttribute.ID, Integer.toString(device));
        params.put(ResourceKeys.IMAGE_HASH, hash);

        JSONObject jsonRequest = new JSONObject(params);

        return new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new BaseResponseListener(context) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                super.onSuccess(response);

                String imageData = response.getString(SwiftResponse.DATA_FIELD);

                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                File dir = new File(context.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                boolean created = dir.mkdirs();

                if(created) {
                    Log.v(this.getClass().getName(), "image directory has been created");
                }

                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(dir, device + ".jpg"));
                    outputStream.write(decodedString);
                    outputStream.close();

                    if(imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch(IOException e) {
                    Log.w(this.getClass().getName(), "writing image data failed: " + e);
                }
            }
        }, new BaseErrorListener(context));
    }

    /**
     * Generates a map for the request parameters. It will already contain the parameters
     * for the designated action, the country key and - if activated - the user authentication parameters.
     * @param context Context
     * @param action designated server action
     * @param userAuthentication adds user authentication parameters to map if true
     * @return Map with some standard parameters
     */
    public static HashMap<String, String> generateParameterMap(Context context, @Nullable String action, boolean userAuthentication) {
        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        HashMap<String, String> parameterMap = new HashMap<>();

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            parameterMap.put(Defaults.VERSION_CODE_KEY, Integer.toString(pInfo.versionCode));
        } catch(PackageManager.NameNotFoundException e) {
            parameterMap.put(Defaults.VERSION_CODE_KEY, Integer.toString(-1));
        }

        if (action != null) {
            parameterMap.put(Defaults.ACTION_KEY, action);
        }
        parameterMap.put(Defaults.COUNTRY_KEY, preferences.getString(Defaults.COUNTRY_PREFERENCE, null));

        if(userAuthentication) {
            parameterMap.put(Defaults.AUTH_ID_KEY, Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
            parameterMap.put(Defaults.AUTH_PW_KEY, preferences.getString(Defaults.PW_PREFERENCE, null));
        }

        return parameterMap;
    }
}
