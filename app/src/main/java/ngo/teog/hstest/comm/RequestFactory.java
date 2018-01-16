package ngo.teog.hstest.comm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.hstest.DeviceInfoActivity;
import ngo.teog.hstest.MainActivity;
import ngo.teog.hstest.NewDeviceActivity;
import ngo.teog.hstest.R;
import ngo.teog.hstest.helpers.Defaults;
import ngo.teog.hstest.helpers.DeviceFilter;
import ngo.teog.hstest.helpers.ResponseParser;
import ngo.teog.hstest.helpers.HospitalDevice;
import ngo.teog.hstest.helpers.UserFilter;
import ngo.teog.hstest.helpers.ResponseException;

/**
 * Created by Julian on 13.12.2017.
 */

//TODO könnte auch ein Singleton werden
public class RequestFactory {
    public DeviceOpenRequest createDeviceOpenRequest(Context context, View disable, View enable, DeviceFilter[] filters) {
        String url = DeviceOpenRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        url = appendGETParameter(url, UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        url = appendGETParameter(url, UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        if(filters != null) {
            for (DeviceFilter filter : filters) {
                url = appendGETParameter(url, filter.getType(), filter.getValue());
            }
        }

        return new DeviceOpenRequest(context, disable, enable, url);
    }

    public class DeviceOpenRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/devices.php?action=fetch";

        public DeviceOpenRequest(final Context context, final View disable, final View enable, final String url) {
            super(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<HospitalDevice> deviceList = new ResponseParser().parseDeviceList(response);

                        if(deviceList.size() > 0) {
                            Intent intent = new Intent(context, DeviceInfoActivity.class);
                            intent.putExtra("device", deviceList.get(0));
                            context.startActivity(intent);
                        } else {
                            throw new ResponseException("device not found");
                        }
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public DeviceListRequest createDeviceRequest(Context context, View disable, View enable, DeviceFilter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        String url = DeviceListRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        url = appendGETParameter(url, UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        url = appendGETParameter(url, UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        if(filters != null) {
            for (DeviceFilter filter : filters) {
                url = appendGETParameter(url, filter.getType(), filter.getValue());
            }
        }

        return new DeviceListRequest(context, disable, enable, url, adapter);
    }

    public class DeviceListRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/devices.php?action=fetch";

        public DeviceListRequest(final Context context, final View disable, final View enable, final String url, final ArrayAdapter<HospitalDevice> adapter) {
            super(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(adapter != null) {
                        adapter.clear();
                    }

                    try {
                        if(adapter != null) {
                            adapter.addAll(new ResponseParser().parseDeviceList(response));
                        }
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    adapter.clear();

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public LoginRequest createLoginRequest(Activity context, View disable, View enable, String mail, String password) {
        String url = LoginRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        url = appendGETParameter(url, UserFilter.MAIL, mail);
        url = appendGETParameter(url, UserFilter.PASSWORD, password);

        return new LoginRequest(context, disable, enable, url, preferences);
    }

    public class LoginRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/users.php?action=login";

        //Der Kontext muss hier eine Activity sein, da diese am Ende gefinishet wird.
        public LoginRequest(final Activity context, final View disable, final View enable, final String url, final SharedPreferences preferences) {
            super(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        new ResponseParser().parseDefaultResponse(response);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(context.getString(R.string.id_pref), -1);
                        editor.putString(context.getString(R.string.pw_pref), "dummyPW");
                        editor.commit();

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);

                        //finishen nicht vergessen, damit die Activity aus dem Stack entfernt wird
                        context.finish();
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public DeviceCreationRequest createDeviceCreationRequest(Context context, View disable, View enable, final HospitalDevice device, final Bitmap bitmap, String ward) {

        String url = DeviceCreationRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        url = appendGETParameter(url, UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        url = appendGETParameter(url, UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        url = appendGETParameter(url, DeviceFilter.ASSET_NUMBER, device.getAssetNumber());
        url = appendGETParameter(url, DeviceFilter.TYPE, device.getType());
        url = appendGETParameter(url, DeviceFilter.SERIAL_NUMBER, device.getSerialNumber());
        url = appendGETParameter(url, DeviceFilter.MANUFACTURER, device.getManufacturer());
        url = appendGETParameter(url, DeviceFilter.MODEL, device.getModel());
        url = appendGETParameter(url, "ward", ward);

        return new DeviceCreationRequest(context, url, disable, enable) {
            @Override
            protected Map<String, String> getParams() {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();//TODO closen
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageBytes = stream.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                Map<String, String> params = new HashMap<>();
                params.put(DeviceFilter.ID, Integer.toString(device.getID()));
                params.put(DeviceFilter.ASSET_NUMBER, device.getAssetNumber());
                params.put(DeviceFilter.TYPE, device.getType());
                params.put(DeviceFilter.SERIAL_NUMBER, device.getSerialNumber());
                params.put(DeviceFilter.MANUFACTURER, device.getManufacturer());
                params.put("image", encodedImage);//TODO die Identifier anders organisieren

                return params;
            }
        };
    }

    public class DeviceCreationRequest extends JsonObjectRequest {

        private static final String BASE_URL = "https://teog.virlep.de/devices.php?action=create";

        public DeviceCreationRequest(final Context context, final String url, final View disable, final View enable) {
            super(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<HospitalDevice> deviceList = new ResponseParser().parseDeviceList(response);

                        Intent intent = new Intent(context, DeviceInfoActivity.class);
                        intent.putExtra("device", deviceList.get(0));
                        context.startActivity(intent);
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String appendGETParameter(String url, String parameter, String value) {
        return url + "&" + parameter + "=" + value;
    }
}
