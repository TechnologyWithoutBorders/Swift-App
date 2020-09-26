package ngo.teog.swift.communication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.swift.R;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.UserFilter;

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

    private RequestFactory() {}

    public DeviceImageUploadRequest createDeviceImageUploadRequest(final Context context, final int deviceId, final Bitmap bitmap) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DataAction.UPLOAD_DEVICE_IMAGE, true);

        params.put(ResourceKeys.DEVICE_ID, Integer.toString(deviceId));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        try {
            stream.close();
        } catch(IOException e) {
            //ignore
        }
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        params.put(ResourceKeys.IMAGE, encodedImage);

        JSONObject request = new JSONObject(params);

        return new DeviceImageUploadRequest(url, request);
    }

    public class DeviceImageUploadRequest extends JsonObjectRequest {

        public DeviceImageUploadRequest(final String url, JSONObject request) {
            super(Request.Method.POST, url, request, response -> {
                //TODO
                try {
                    Log.d("IMAGE_UPLOAD", response.toString(4));
                } catch (JSONException e) {
                    Log.e("IMAGE_UPLOAD", "response not readable", e);
                }
            }, error -> Log.e("IMAGE_UPLOAD", error.toString(), error));
        }
    }

    public DefaultRequest createDeviceImageRequest(final Context context, View disable, final View enable, final int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceAction.FETCH_DEVICE_IMAGE, true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                String imageData = response.getString(SwiftResponse.DATA_FIELD);

                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                FileOutputStream outputStream;

                try {
                    File dir = new File(context.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                    dir.mkdirs();

                    outputStream = new FileOutputStream(new File(dir, id + ".jpg"));
                    outputStream.write(decodedString);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((ImageView) enable).setImageBitmap(bitmap);
            }
        });
    }

    public LoginRequest createLoginRequest(Activity context, AnimationDrawable anim, LinearLayout form, String mail, String password, String country) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserAction.LOGIN_USER, false);

        params.put(UserFilter.MAIL, mail);
        params.put(UserFilter.PASSWORD, password);
        //Override country, because the shared preferences contain no country at this point
        params.put(Defaults.COUNTRY_KEY, country);

        JSONObject request = new JSONObject(params);

        return new LoginRequest(context, anim, form, url, request, password, country);
    }

    public PasswordResetRequest createPasswordResetRequest(Context context, String mail, String country) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserAction.RESET_PASSWORD, false);

        params.put(UserFilter.MAIL, mail);
        //Override country, because the shared preferences contain no country at this point
        params.put(Defaults.COUNTRY_KEY, country);

        JSONObject request = new JSONObject(params);

        return new PasswordResetRequest(context, url, request);
    }

    public ImageHashRequest createImageHashRequest(Context context, int device, String hash, ImageView imageView) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceAction.FETCH_DEVICE_IMAGE_HASH, true);

        params.put(DeviceFilter.ID, Integer.toString(device));
        params.put(ResourceKeys.IMAGE_HASH, hash);

        JSONObject request = new JSONObject(params);

        return new ImageHashRequest(context, url, request, device, imageView);
    }

    public class LoginRequest extends JsonObjectRequest {

        //use activity instead of plain context, because it must be removed from the stack afterwards
        public LoginRequest(final Activity activity, final AnimationDrawable anim, final LinearLayout form, final String url, JSONObject request, final String password, final String country) {
            super(Request.Method.POST, url, request, response -> {
                try {
                    int id = ResponseParser.parseLoginResponse(response);

                    SharedPreferences preferences = activity.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(Defaults.ID_PREFERENCE, id);
                    editor.putString(Defaults.PW_PREFERENCE, password);
                    editor.putString(Defaults.COUNTRY_PREFERENCE, country);
                    editor.apply();

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);

                    //remove activity from stack
                    activity.finish();
                } catch(TransparentServerException e) {
                    //this typically applies if the login data was incorrect
                    Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    anim.stop();
                    form.setVisibility(View.VISIBLE);
                } catch(ServerException e) {
                    Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.server_comm_error_message), Toast.LENGTH_SHORT).show();
                    anim.stop();
                    form.setVisibility(View.VISIBLE);
                } catch(Exception e) {
                    Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                    anim.stop();
                    form.setVisibility(View.VISIBLE);
                }
            }, error -> {
                anim.stop();
                form.setVisibility(View.VISIBLE);
                Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public class PasswordResetRequest extends JsonObjectRequest {
        public PasswordResetRequest(final Context context, final String url, JSONObject request) {
            super(Request.Method.POST, url, request, response -> {
                try {
                    int responseCode = response.getInt(SwiftResponse.CODE_FIELD);

                    switch(responseCode) {
                        case SwiftResponse.CODE_OK:
                            Toast.makeText(context.getApplicationContext(), "e-mail has been sent", Toast.LENGTH_SHORT).show();

                            break;
                        case SwiftResponse.CODE_FAILED_VISIBLE:
                            throw new TransparentServerException(response.getString(SwiftResponse.DATA_FIELD));
                        case SwiftResponse.CODE_FAILED_HIDDEN:
                        default:
                            throw new ServerException(response.getString(SwiftResponse.DATA_FIELD));
                    }
                } catch(TransparentServerException e) {
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                }
            }, error -> Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Server request that uploads a device image hash in order to check if a newer image is available.
     */
    public class ImageHashRequest extends JsonObjectRequest {
        public ImageHashRequest(final Context context, final String url, JSONObject request, final int device, ImageView imageView) {
            super(Request.Method.POST, url, request, response -> {
                try {
                    int responseCode = response.getInt(SwiftResponse.CODE_FIELD);

                    switch(responseCode) {
                        case SwiftResponse.CODE_OK:
                            String imageData = response.getString(SwiftResponse.DATA_FIELD);

                            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            File dir = new File(context.getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                            dir.mkdirs();

                            FileOutputStream outputStream = new FileOutputStream(new File(dir, device + ".jpg"));
                            outputStream.write(decodedString);
                            outputStream.close();

                            if(imageView != null) {
                                imageView.setImageBitmap(bitmap);
                            }

                            break;
                        case SwiftResponse.CODE_FAILED_VISIBLE:
                            throw new TransparentServerException(response.getString(SwiftResponse.DATA_FIELD));
                        case SwiftResponse.CODE_FAILED_HIDDEN:
                        default:
                            throw new ServerException(response.getString(SwiftResponse.DATA_FIELD));
                    }
                } catch(TransparentServerException e) {
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                }
            }, error -> Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show());
        }
    }

    public DeviceDocumentRequest createDeviceDocumentRequest(Context context, HospitalDevice device, ImageView button, ProgressBar progressBar) {
        final String url = Defaults.BASE_URL + Defaults.DOCUMENTS_URL;

        Map<String, String> params = new HashMap<>();
        params.put(DeviceFilter.MANUFACTURER, device.getManufacturer());
        params.put(DeviceFilter.MODEL, device.getModel());

        JSONObject request = new JSONObject(params);

        return new DeviceDocumentRequest(context, url, request, device, button, progressBar);
    }

    public class DeviceDocumentRequest extends JsonObjectRequest {
        public DeviceDocumentRequest(final Context context, final String url, JSONObject request, HospitalDevice device, ImageView button, ProgressBar progressBar) {
            super(Request.Method.POST, url, request, response -> {
                try {
                    int responseCode = response.getInt(SwiftResponse.CODE_FIELD);

                    switch(responseCode) {
                        case SwiftResponse.CODE_OK:
                            //The response provides a list of links to matching documents.
                            JSONArray documentList = response.getJSONArray(SwiftResponse.DATA_FIELD);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Available Documents");

                            final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);

                            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            });

                            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Defaults.BASE_URL + "device_documents/" + device.getManufacturer() + "/" + device.getModel() + "/" + adapter.getItem(i))));
                                }
                            });

                            AlertDialog dialog = builder.create();

                            for(int i = 0; i < documentList.length(); i++) {
                                String docLink = documentList.getString(i);

                                adapter.add(docLink);
                            }

                            adapter.notifyDataSetChanged();
                            dialog.show();

                            break;
                        case SwiftResponse.CODE_FAILED_VISIBLE:
                            throw new TransparentServerException(response.getString(SwiftResponse.DATA_FIELD));
                        case SwiftResponse.CODE_FAILED_HIDDEN:
                        default:
                            throw new ServerException(response.getString(SwiftResponse.DATA_FIELD));
                    }
                } catch(TransparentServerException e) {
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
            }, new BaseErrorListener(context, progressBar, button));
        }
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
