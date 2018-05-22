package ngo.teog.swift.communication;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import ngo.teog.swift.gui.DeviceInfoActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.ReportInfoActivity;
import ngo.teog.swift.gui.main.TodoFragment;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.Filter;
import ngo.teog.swift.helpers.NewsItem;
import ngo.teog.swift.helpers.Report;
import ngo.teog.swift.helpers.filters.ReportFilter;
import ngo.teog.swift.helpers.ResponseParser;
import ngo.teog.swift.helpers.HospitalDevice;
import ngo.teog.swift.helpers.filters.UserFilter;
import ngo.teog.swift.helpers.ResponseException;

/**
 * @author Julian Deyerler
 */

//TODO könnte auch ein Singleton werden
public class RequestFactory {
    public DeviceImageRequest createDeviceImageRequest(Context context, View disable, View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "image", true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DeviceImageRequest(context, disable, enable, url, request);
    }

    public class DeviceImageRequest extends JsonObjectRequest {
        public DeviceImageRequest(final Context context, final View disable, final View enable, final String url, JSONObject request) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int responseCode = response.getInt("response_code");
                        switch(responseCode) {
                            case ngo.teog.swift.helpers.Response.CODE_OK:
                                String imageData = response.getString("data");

                                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                ((ImageView)enable).setImageBitmap(bitmap);

                                break;
                            case ngo.teog.swift.helpers.Response.CODE_FAILED_VISIBLE:
                                throw new ResponseException(response.getString("data"));
                            case ngo.teog.swift.helpers.Response.CODE_FAILED_HIDDEN:
                            default:
                                throw new Exception(response.getString("data"));
                        }
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("image fetch", "something went wrong", e);
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

    public DeviceOpenRequest createDeviceOpenRequest(Context context, View disable, View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "fetch", true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DeviceOpenRequest(context, disable, enable, url, request);
    }

    public class DeviceOpenRequest extends JsonObjectRequest {

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
        final String url = Defaults.BASE_URL + Defaults.NEWS_URL;

        Map<String, String> params = generateParameterMap(context, "fetch", true);

        JSONObject request = new JSONObject(params);

        return new NewsListRequest(context, url, request);
    }

    public class NewsListRequest extends JsonObjectRequest {

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
                                    .setContentTitle("Fake news from Swift")
                                    .setContentText("Tap to show messages");
                            Intent resultIntent = new Intent(context, MainActivity.class);
                            resultIntent.putExtra("NEWS", news);
                            resultIntent.putExtra("notification", mNotificationId);

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

    public DeviceListRequest createDeviceRequest(Context context, View disable, View enable, Filter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "fetch", true);

        if(filters != null) {
            for (Filter filter : filters) {
                params.put(filter.getType(), filter.getValue());
            }
        }

        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public class DeviceListRequest extends JsonObjectRequest {

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

    public DeviceListRequest createDeviceSearchRequest(Context context, View disable, View enable, Filter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "search", true);

        if(filters != null) {
            for (Filter filter : filters) {
                params.put(filter.getType(), filter.getValue());
            }
        }

        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public LoginRequest createLoginRequest(Activity context, View disable, View enable, String mail, String password) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, "login", false);

        params.put(UserFilter.MAIL, mail);
        params.put(UserFilter.PASSWORD, password);

        JSONObject request = new JSONObject(params);

        return new LoginRequest(context, disable, enable, url, request, password);
    }

    //TODO beim LoginRequest sollte auch die zuletzt zugestellte News-ID abgerufen werden
    public class LoginRequest extends JsonObjectRequest {

        //Der Kontext muss hier eine Activity sein, da diese am Ende gefinishet wird.
        public LoginRequest(final Activity context, final View disable, final View enable, final String url, JSONObject request, final String password) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int id = new ResponseParser().parseLoginResponse(response);

                        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(Defaults.ID_PREFERENCE, id);
                        editor.putString(Defaults.PW_PREFERENCE, password);
                        editor.putInt(Defaults.LAST_NEWS_PREF, -1);
                        editor.apply();

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
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "create", true);

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

    public ReportCreationRequest createReportCreationRequest(final Context context, View disable, View enable, final Report report) {
        final String url = Defaults.BASE_URL + Defaults.REPORTS_URL;

        Map<String, String> params = generateParameterMap(context, "create", true);

        params.put(ReportFilter.AUTHOR, Integer.toString(report.getAuthor()));
        params.put(ReportFilter.DEVICE, Integer.toString(report.getDevice()));
        params.put(ReportFilter.TITLE, report.getTitle());

        JSONObject request = new JSONObject(params);

        return new ReportCreationRequest(context, url, request, disable, enable);
    }

    public class ReportCreationRequest extends JsonObjectRequest {

        public ReportCreationRequest(final Context context, final String url, JSONObject request, final View disable, final View enable) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<Report> deviceList = new ResponseParser().parseReportList(response);

                        Intent intent = new Intent(context, ReportInfoActivity.class);
                        intent.putExtra("REPORT", deviceList.get(0));
                        context.startActivity(intent);
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private HashMap<String, String> generateParameterMap(Context context, String action, boolean userValidation) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("action", action);

        if(userValidation) {
            SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

            parameterMap.put(UserFilter.ID, Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
            parameterMap.put(UserFilter.PASSWORD, preferences.getString(Defaults.PW_PREFERENCE, null));
        }

        return parameterMap;
    }
}
