package ngo.teog.swift.communication;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.swift.gui.DeviceInfoActivity;
import ngo.teog.swift.gui.ImageActivity;
import ngo.teog.swift.gui.UserInfoActivity;
import ngo.teog.swift.gui.main.MainActivity;
import ngo.teog.swift.R;
import ngo.teog.swift.gui.ReportInfoActivity;
import ngo.teog.swift.gui.main.TodoFragment;
import ngo.teog.swift.helpers.Debugging;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.SearchObject;
import ngo.teog.swift.helpers.User;
import ngo.teog.swift.helpers.filters.DeviceFilter;
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
    public class DefaultRequest extends JsonObjectRequest {
        public DefaultRequest(Context context, String url, JSONObject request, View disable, View enable, BaseResponseListener listener) {
            super(Request.Method.POST, url, request, listener, new BaseErrorListener(context, disable, enable));
        }
    }

    private abstract class BaseResponseListener implements Response.Listener<JSONObject> {
        private Context context;
        private View disable;
        private View enable;

        public BaseResponseListener(Context context, View disable, View enable) {
            this.context = context;
            this.disable = disable;
            this.enable = enable;
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                int responseCode = response.getInt("response_code");
                switch(responseCode) {
                    case ngo.teog.swift.helpers.Response.CODE_OK:
                        onSuccess(response);

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
                if(Debugging.showSystemExceptions) {
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            if(disable != null) {
                disable.setVisibility(View.INVISIBLE);
            }

            if(enable != null) {
                enable.setVisibility(View.VISIBLE);
            }
        }

        public abstract void onSuccess(JSONObject response) throws Exception;
    }

    private class BaseErrorListener implements Response.ErrorListener {
        private Context context;
        private View disable;
        private View enable;

        public BaseErrorListener(Context context, View disable, View enable) {
            this.context = context;
            this.disable = disable;
            this.enable = enable;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if(disable != null) {
                disable.setVisibility(View.INVISIBLE);
            }

            if(enable != null) {
                enable.setVisibility(View.VISIBLE);
            }

            if(Debugging.showSystemExceptions) {
                Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public DefaultRequest createDeviceImageHashRequest(Context context, View disable, final View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "fetch_image_hash", true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                String imageHash = response.getString("data");

                //TODO
            }
        });
    }

    public DefaultRequest createDeviceImageRequest(final DeviceInfoActivity context, View disable, final View enable, final int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceFilter.ACTION_FETCH_DEVICE_IMAGE, true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                String imageData = response.getString("data");

                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                FileOutputStream outputStream;

                try {
                    outputStream = context.openFileOutput("image_" + Integer.toString(id) + ".jpg", Context.MODE_PRIVATE);
                    outputStream.write(decodedString);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((ImageView) enable).setImageBitmap(bitmap);
                enable.setBackgroundColor(Color.BLACK);

                enable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File image = new File(context.getFilesDir(), "image_" + Integer.toString(id) + ".jpg");

                        if(image.exists()) {
                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra("IMAGE", image);

                            context.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    public DefaultRequest createUserImageRequest(Context context, View disable, final View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserFilter.ACTION_FETCH_USER_IMAGE, true);
        params.put(UserFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                String imageData = response.getString("data");

                byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ((ImageView) enable).setImageBitmap(bitmap);
            }
        });
    }

    public DefaultRequest createDeviceOpenRequest(final Context context, View disable, View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceFilter.ACTION_FETCH_DEVICE, true);
        params.put(DeviceFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<HospitalDevice> deviceList = new ResponseParser().parseDeviceList(response);

                if(deviceList.size() > 0) {
                    Intent intent = new Intent(context, DeviceInfoActivity.class);
                    intent.putExtra("device", deviceList.get(0));
                    context.startActivity(intent);
                } else {
                    throw new ResponseException("device not found");
                }
            }
        });
    }

    public DefaultRequest createUserOpenRequest(final Context context, View disable, View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserFilter.ACTION_FETCH_USER, true);
        params.put(UserFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<User> userList = new ResponseParser().parseUserList(response);

                if(userList.size() > 0) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("user", userList.get(0));
                    context.startActivity(intent);
                } else {
                    throw new ResponseException("user not found");
                }
            }
        });
    }

    public DefaultRequest createReportOpenRequest(final Context context, View disable, View enable, int id) {
        final String url = Defaults.BASE_URL + Defaults.REPORTS_URL;

        Map<String, String> params = generateParameterMap(context, ReportFilter.ACTION_FETCH_REPORT, true);
        params.put(ReportFilter.ID, Integer.toString(id));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<Report> reportList = new ResponseParser().parseReportList(response);

                if(reportList.size() > 0) {
                    Intent intent = new Intent(context, ReportInfoActivity.class);
                    intent.putExtra("REPORT", reportList.get(0));
                    context.startActivity(intent);
                } else {
                    throw new ResponseException("report not found");
                }
            }
        });
    }

    /*public DefaultRequest createNewsRequest(final Context context) {
        final String url = Defaults.BASE_URL + Defaults.NEWS_URL;

        Map<String, String> params = generateParameterMap(context, "fetch", true);

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, null, null, new BaseResponseListener(context, null, null) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
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
            }
        });
    }*/

    public DefaultRequest createWorkRequest(final Context context, int id, final int notificationCounter) {
        final String url = Defaults.BASE_URL + Defaults.NEWS_URL;

        Map<String, String> params = generateParameterMap(context, "fetch_work", true);
        params.put(UserFilter.ID, Integer.toString(id));
        params.put("notification_counter", Integer.toString(notificationCounter));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, null, null, new BaseResponseListener(context, null, null) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<Report> reportList = new ArrayList<>();

                int responseCode = response.getInt(ngo.teog.swift.helpers.Response.CODE_FIELD);
                switch(responseCode) {
                    case ngo.teog.swift.helpers.Response.CODE_OK:
                        JSONObject data = response.getJSONObject(ngo.teog.swift.helpers.Response.DATA_FIELD);
                        int notificationID = data.getInt("notification_counter");

                        JSONArray jsonReportList = data.getJSONArray("report_list");

                        for(int i = 0; i < jsonReportList.length(); i++) {
                            JSONObject reportObject = jsonReportList.getJSONObject(i);

                            int id = reportObject.getInt(ReportFilter.ID);
                            int author = reportObject.getInt(ReportFilter.AUTHOR);
                            int device = reportObject.getInt(ReportFilter.DEVICE);
                            int previousState = reportObject.getInt(ReportFilter.PREVIOUS_STATE);
                            int currentState = reportObject.getInt(ReportFilter.CURRENT_STATE);
                            String description = reportObject.getString(ReportFilter.DESCRIPTION);
                            String dateString = reportObject.getString(ReportFilter.DATETIME);

                            Date date = Report.reportFormat.parse(dateString);

                            Report report = new Report(id, author, null, device, previousState, currentState, description , date);
                            reportList.add(report);
                        }

                        if(reportList.size() > 0) {
                            final String CHANNEL_ID = "work_channel";
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setContentTitle("TeoG Swift")
                                    .setVibrate(new long[] {0, 1000})
                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                    .setContentText(reportList.size() + " devices need your attention.")
                                    .setAutoCancel(true);
                            Intent resultIntent = new Intent(context, MainActivity.class);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(0, mBuilder.build());
                        }

                        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(Defaults.NOTIFICATION_COUNTER, notificationID);
                        editor.apply();

                        break;
                    case ngo.teog.swift.helpers.Response.CODE_FAILED_VISIBLE:
                        throw new ResponseException(response.getString(ngo.teog.swift.helpers.Response.DATA_FIELD));
                    case ngo.teog.swift.helpers.Response.CODE_FAILED_HIDDEN:
                    default:
                        throw new Exception(response.getString(ngo.teog.swift.helpers.Response.DATA_FIELD));
                }
            }
        });
    }

    public DeviceListRequest createTodoListRequest(Context context, View disable, View enable, ArrayAdapter<SearchObject> adapter) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceFilter.ACTION_FETCH_TODO_LIST, true);
        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public DeviceListRequest createDeviceListRequest(Context context, View disable, View enable, ArrayAdapter<SearchObject> adapter, int user) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "fetch_device_list", true);
        params.put(UserFilter.ID, Integer.toString(user));
        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public class DeviceListRequest extends JsonObjectRequest {

        public DeviceListRequest(final Context context, final View disable, final View enable, final String url, JSONObject request, final ArrayAdapter<SearchObject> adapter) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int responseCode = response.getInt("response_code");
                        switch(responseCode) {
                            case ngo.teog.swift.helpers.Response.CODE_OK:
                                if(adapter != null) {
                                    adapter.clear();
                                }

                                if(adapter != null) {
                                    adapter.addAll(new ResponseParser().parseDeviceList(response));
                                }

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
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public ColleagueRequest createColleagueRequest(Context context, View disable, View enable, ArrayAdapter<User> userAdapter, int user) {
        final String url = Defaults.BASE_URL + Defaults.HOSPITALS_URL;

        Map<String, String> params = generateParameterMap(context, "fetch_members", true);
        params.put(UserFilter.ID, Integer.toString(user));
        JSONObject request = new JSONObject(params);

        return new ColleagueRequest(context, disable, enable, url, request, userAdapter);
    }

    public class ColleagueRequest extends JsonObjectRequest {
        public ColleagueRequest(final Context context, final View disable, final View enable, final String url, JSONObject request, final ArrayAdapter<User> userAdapter) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int responseCode = response.getInt("response_code");
                        switch(responseCode) {
                            case ngo.teog.swift.helpers.Response.CODE_OK:
                                if(userAdapter != null) {
                                    userAdapter.clear();
                                }

                                if(userAdapter != null) {
                                    userAdapter.addAll(new ResponseParser().parseUserList(response));
                                }

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
                    }

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userAdapter.clear();

                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public UnsubscriptionOptionalUpdateRequest createUnsubscriptionOptionalUpdateRequest(Context context, MenuItem item, boolean toggle, int user, int device) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "update_unsubscription", true);
        params.put(UserFilter.ID, Integer.toString(user));
        params.put(DeviceFilter.ID, Integer.toString(device));
        params.put("toggle", Boolean.toString(toggle));

        JSONObject request = new JSONObject(params);

        return new UnsubscriptionOptionalUpdateRequest(context, url, request, item);
    }

    public class UnsubscriptionOptionalUpdateRequest extends JsonObjectRequest {

        public UnsubscriptionOptionalUpdateRequest(final Context context, final String url, JSONObject request, final MenuItem item) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int responseCode = response.getInt("response_code");
                        switch(responseCode) {
                            case ngo.teog.swift.helpers.Response.CODE_OK:
                                int unsubscriptions = response.getInt(ngo.teog.swift.helpers.Response.DATA_FIELD);

                                if(unsubscriptions > 0) {
                                    item.setIcon(context.getResources().getDrawable(R.drawable.ic_notifications_off_white_24dp));
                                } else {
                                    item.setIcon(context.getResources().getDrawable(R.drawable.ic_notifications_white_24dp));
                                }

                                item.setActionView(null);

                                break;
                            case ngo.teog.swift.helpers.Response.CODE_FAILED_VISIBLE:
                                throw new ResponseException(response.getString("data"));
                            case ngo.teog.swift.helpers.Response.CODE_FAILED_HIDDEN:
                            default:
                                throw new Exception(response.getString("data"));
                        }
                    } catch(ResponseException e) {
                        item.setActionView(null);
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        item.setActionView(null);
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    item.setActionView(null);
                    Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public DeviceListRequest createDeviceSearchRequest(Context context, View disable, View enable, String searchValue, ArrayAdapter<SearchObject> adapter) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceFilter.ACTION_SEARCH_DEVICE, true);
        params.put(DeviceFilter.TYPE, searchValue);

        JSONObject request = new JSONObject(params);

        return new DeviceListRequest(context, disable, enable, url, request, adapter);
    }

    public LoginRequest createLoginRequest(Activity context, AnimationDrawable anim, LinearLayout form, String mail, String password, String country) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserFilter.ACTION_LOGIN_USER, false);

        params.put(UserFilter.MAIL, mail);
        params.put(UserFilter.PASSWORD, password);

        JSONObject request = new JSONObject(params);

        return new LoginRequest(context, anim, form, url, request, password, country);
    }

    public UserListRequest createUserSearchRequest(Context context, View disable, View enable, String searchValue, ArrayAdapter<SearchObject> adapter) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserFilter.ACTION_SEARCH_USER, true);
        params.put(UserFilter.FULL_NAME, searchValue);

        JSONObject request = new JSONObject(params);

        return new UserListRequest(context, disable, enable, url, request, adapter);
    }

    public class UserListRequest extends JsonObjectRequest {

        public UserListRequest(final Context context, final View disable, final View enable, final String url, JSONObject request, final ArrayAdapter<SearchObject> adapter) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(adapter != null) {
                        adapter.clear();
                    }

                    try {
                        if(adapter != null) {
                            adapter.addAll(new ResponseParser().parseUserList(response));
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

    public class LoginRequest extends JsonObjectRequest {

        //Der Kontext muss hier eine Activity sein, da diese am Ende gefinishet wird.
        public LoginRequest(final Activity context, final AnimationDrawable anim, final LinearLayout form, final String url, JSONObject request, final String password, final String country) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int id = new ResponseParser().parseLoginResponse(response);

                        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(Defaults.ID_PREFERENCE, id);
                        editor.putString(Defaults.PW_PREFERENCE, password);
                        editor.putString(Defaults.COUNTRY_PREFERENCE, country);
                        editor.putInt(Defaults.NOTIFICATION_COUNTER, 0);
                        editor.apply();

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);

                        //finishen nicht vergessen, damit die Activity aus dem Stack entfernt wird
                        context.finish();
                    } catch(ResponseException e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        anim.stop();
                        form.setVisibility(View.VISIBLE);
                    } catch(Exception e) {
                        Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN", "failed", e);
                        anim.stop();
                        form.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    anim.stop();
                    form.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN", error.toString());
                }
            });
        }
    }

    public DefaultRequest createProfileUpdateRequest(final Context context, View disable, final View enable, User user) {
        final String url = Defaults.BASE_URL + Defaults.USERS_URL;

        Map<String, String> params = generateParameterMap(context, UserFilter.ACTION_UPDATE_USER, true);

        params.put(UserFilter.ID, Integer.toString(user.getID()));
        params.put(UserFilter.PHONE, user.getPhone());
        params.put("u_position", user.getPosition());

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) {
                enable.setEnabled(false);
            }
        });
    }

    public DefaultRequest createDeviceUpdateRequest(final Context context, View disable, final View enable, int deviceID, int maintenanceInterval) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, "update_maintenance_interval", true);

        params.put(DeviceFilter.ID, Integer.toString(deviceID));
        params.put("d_maintenance_interval", Integer.toString(maintenanceInterval));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ((TextView)enable).setText(Integer.toString(response.getInt("data")) + " Weeks");
            }
        });
    }

    public ReportListRequest createReportListRequest(Context context, View disable, View enable, int deviceID, ArrayAdapter<Report> adapter) {
        final String url = Defaults.BASE_URL + Defaults.REPORTS_URL;

        Map<String, String> params = generateParameterMap(context, ReportFilter.ACTION_FETCH_REPORT_LIST, true);
        params.put(ReportFilter.DEVICE, Integer.toString(deviceID));

        JSONObject request = new JSONObject(params);

        return new ReportListRequest(context, disable, enable, url, request, adapter);
    }

    public class ReportListRequest extends JsonObjectRequest {

        public ReportListRequest(final Context context, final View disable, final View enable, final String url, JSONObject request, final ArrayAdapter<Report> adapter) {
            super(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(adapter != null) {
                        adapter.clear();
                    }

                    try {
                        if(adapter != null) {
                            adapter.addAll(new ResponseParser().parseReportList(response));
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

    public DefaultRequest createDeviceCreationRequest(final Context context, View disable, View enable, final HospitalDevice device, final Bitmap bitmap, int userID) {
        final String url = Defaults.BASE_URL + Defaults.DEVICES_URL;

        Map<String, String> params = generateParameterMap(context, DeviceFilter.ACTION_CREATE_DEVICE, true);

        params.put(UserFilter.ID, Integer.toString(userID));
        params.put(DeviceFilter.ID, Integer.toString(device.getID()));
        params.put(DeviceFilter.ASSET_NUMBER, device.getAssetNumber());
        params.put(DeviceFilter.TYPE, device.getType());
        params.put(DeviceFilter.SERIAL_NUMBER, device.getSerialNumber());
        params.put(DeviceFilter.MANUFACTURER, device.getManufacturer());
        params.put(DeviceFilter.MODEL, device.getModel());
        params.put("d_maintenance_interval", Integer.toString(device.getMaintenanceInterval()));

        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();//TODO closen
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            params.put("image", encodedImage);//TODO die Identifier anders organisieren
        }

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<HospitalDevice> deviceList = new ResponseParser().parseDeviceList(response);

                Intent intent = new Intent(context, DeviceInfoActivity.class);
                intent.putExtra("device", deviceList.get(0));
                context.startActivity(intent);
            }
        });
    }

    public DefaultRequest createReportCreationRequest(final Context context, View disable, View enable, final Report report) {
        final String url = Defaults.BASE_URL + Defaults.REPORTS_URL;

        Map<String, String> params = generateParameterMap(context, ReportFilter.ACTION_CREATE_REPORT, true);

        params.put(ReportFilter.AUTHOR, Integer.toString(report.getAuthor()));
        params.put(ReportFilter.DEVICE, Integer.toString(report.getDevice()));
        params.put(ReportFilter.DESCRIPTION, report.getDescription());
        params.put(ReportFilter.PREVIOUS_STATE, Integer.toString(report.getPreviousState()));//TODO zur current state umbenennen
        params.put(ReportFilter.CURRENT_STATE, Integer.toString(report.getCurrentState()));

        JSONObject request = new JSONObject(params);

        return new DefaultRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws Exception {
                ArrayList<Report> reportList = new ResponseParser().parseReportList(response);

                Intent intent = new Intent(context, ReportInfoActivity.class);
                intent.putExtra("REPORT", reportList.get(0));
                context.startActivity(intent);
            }
        });
    }

    private HashMap<String, String> generateParameterMap(Context context, String action, boolean userValidation) {
        SharedPreferences preferences = context.getSharedPreferences(Defaults.PREF_FILE_KEY, Context.MODE_PRIVATE);

        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("action", action);
        parameterMap.put("country", preferences.getString(Defaults.COUNTRY_PREFERENCE, null));

        if(userValidation) {
            parameterMap.put("validation_id", Integer.toString(preferences.getInt(Defaults.ID_PREFERENCE, -1)));
            parameterMap.put("validation_pw", preferences.getString(Defaults.PW_PREFERENCE, null));
        }

        return parameterMap;
    }
}
