package ngo.teog.swift.comm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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

import ngo.teog.swift.DeviceInfoActivity;
import ngo.teog.swift.MainActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.TodoFragment;
import ngo.teog.swift.UserProfileActivity;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.DeviceFilter;
import ngo.teog.swift.helpers.NewsItem;
import ngo.teog.swift.helpers.ResponseParser;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.User;
import ngo.teog.swift.helpers.UserFilter;
import ngo.teog.swift.helpers.ResponseException;

/**
 * Created by Julian on 13.12.2017.
 */

//TODO könnte auch ein Singleton werden
public class RequestFactory {
    public DeviceOpenRequest createDeviceOpenRequest(Context context, View disable, View enable, int id) {
        String url = DeviceOpenRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "fetch");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DeviceOpenRequest(context, disable, enable, url, request);
    }

    public class DeviceOpenRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/devices.php";

        public DeviceOpenRequest(final Context context, final View disable, final View enable, final String url, JSONObject request) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
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

    public NewsListRequest createNewsRequest(Context context) {
        String url = NewsListRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "fetch");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        JSONObject request = new JSONObject(params);

        return new NewsListRequest(context, url, request);
    }

    public class NewsListRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/info.php";

        public NewsListRequest(final Context context, final String url, JSONObject request) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<NewsItem> newsList = new ResponseParser().parseNewsList(response);

                        if(newsList.size() > 0) {
                            //Test Benachrichtigung
                            int mNotificationId = newsList.get(newsList.size()-1).getID();

                            String news = "";

                            for(NewsItem item : newsList) {
                                news += Defaults.DATE_FORMAT.format(item.getDate()) + "\n" + item.getValue() + "\n\n";
                            }

                            String CHANNEL_ID = "news_channel";
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setContentTitle("News")
                                    .setContentText("News");
                            Intent resultIntent = new Intent(context, MainActivity.class);
                            resultIntent.putExtra("NEWS", news);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            mNotificationManager.notify(mNotificationId, mBuilder.build());
                        }
                    } catch(Exception e) {
                        Log.e("ERROR", "", e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", error.toString());
                }
            });
        }
    }

    public DeviceListRequest createDeviceRequest(Context context, View disable, View enable, DeviceFilter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        String url = DeviceListRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "fetch");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        if(filters != null) {
            for (DeviceFilter filter : filters) {
                params.put(filter.getType(), filter.getValue());
            }
        }

        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public class DeviceListRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/devices.php";

        public DeviceListRequest(final Context context, final View disable, final View enable, final String url, JSONObject request, final ArrayAdapter<HospitalDevice> adapter) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
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

    public DeviceListRequest createDeviceSearchRequest(Context context, View disable, View enable, DeviceFilter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        String url = DeviceListRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "search");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        if(filters != null) {
            for (DeviceFilter filter : filters) {
                params.put(filter.getType(), filter.getValue());
            }
        }

        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public LoginRequest createLoginRequest(Activity context, View disable, View enable, String mail, String password) {
        String url = LoginRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "login");
        params.put(UserFilter.MAIL, mail);
        params.put(UserFilter.PASSWORD, password);

        JSONObject request = new JSONObject(params);

        return new LoginRequest(context, disable, enable, url, request, preferences, password);
    }

    public class LoginRequest extends JsonObjectRequest {

        public static final String BASE_URL = "https://teog.virlep.de/users.php";

        //Der Kontext muss hier eine Activity sein, da diese am Ende gefinishet wird.
        public LoginRequest(final Activity context, final View disable, final View enable, final String url, JSONObject request, final SharedPreferences preferences, final String password) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int id = new ResponseParser().parseLoginResponse(response);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(context.getString(R.string.id_pref), id);
                        editor.putString(context.getString(R.string.pw_pref), password);
                        editor.putInt("LAST_NEWS_ID", -1);
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

    public DeviceCreationRequest createDeviceCreationRequest(final Context context, View disable, View enable, final HospitalDevice device, final Bitmap bitmap, String ward) {

        String url = DeviceCreationRequest.BASE_URL;

        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        Map<String, String> params = new HashMap<>();
        params.put("action", "create");
        params.put(UserFilter.ID, Integer.toString(preferences.getInt(context.getString(R.string.id_pref), -1)));
        params.put(UserFilter.PASSWORD, preferences.getString(context.getString(R.string.pw_pref), null));

        params.put(DeviceFilter.ASSET_NUMBER, device.getAssetNumber());
        params.put(DeviceFilter.TYPE, device.getType());
        params.put(DeviceFilter.SERIAL_NUMBER, device.getSerialNumber());
        params.put(DeviceFilter.MANUFACTURER, device.getManufacturer());
        params.put(DeviceFilter.MODEL, device.getModel());
        params.put(DeviceFilter.WORKING, Boolean.toString(device.isWorking()));
        params.put(DeviceFilter.NEXT_MAINTENANCE, TodoFragment.DATE_FORMAT.format(device.getNextMaintenance()));
        params.put("ward", ward);

        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();//TODO closen
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            params.put("image", encodedImage);//TODO die Identifier anders organisieren
        }

        JSONObject request = new JSONObject(params);

        return new DeviceCreationRequest(context, url, request, disable, enable);
    }

    public class DeviceCreationRequest extends JsonObjectRequest {

        private static final String BASE_URL = "https://teog.virlep.de/devices.php";

        public DeviceCreationRequest(final Context context, final String url, JSONObject request, final View disable, final View enable) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
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
}
