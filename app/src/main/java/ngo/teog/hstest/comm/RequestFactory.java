package ngo.teog.hstest.comm;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ngo.teog.hstest.helpers.DeviceFilter;
import ngo.teog.hstest.helpers.DeviceParser;
import ngo.teog.hstest.helpers.HospitalDevice;

/**
 * Created by Julian on 13.12.2017.
 */

//TODO könnte auch ein Singleton werden
public class RequestFactory {
    public DeviceListRequest createDeviceRequest(Context context, View disable, View enable, DeviceFilter[] filters, ArrayAdapter<HospitalDevice> adapter) {
        String url = DeviceListRequest.BASE_URL;

        if(filters != null) {
            for (DeviceFilter filter : filters) {
                //TODO beachten, dass hier schon ein Fragezeichen drinsteht!
                url += "&" + filter.getType() + "=" + filter.getValue();
            }
        }

        return new DeviceListRequest(context, disable, enable, url, adapter);
    }

    public class DeviceListRequest extends JsonArrayRequest {

        public static final String BASE_URL = "https://teog.virlep.de/devices.php?action=fetch";

        public DeviceListRequest(final Context context, final View disable, final View enable, final String url, final ArrayAdapter<HospitalDevice> adapter) {
            super(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    adapter.clear();

                    try {
                        adapter.addAll(new DeviceParser(null).parseDeviceList(response));
                    } catch(JSONException e) {
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

    public DeviceCreationRequest createDeviceCreationRequest(Context context, View disable, View enable, final HospitalDevice device, final Bitmap bitmap, int hospital, String ward) {
        return new DeviceCreationRequest(context, disable, enable) {
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
                params.put(DeviceFilter.SERVICE_NUMBER, device.getServiceNumber());
                params.put(DeviceFilter.MANUFACTURER, device.getManufacturer());
                params.put("image", encodedImage);//TODO die Identifier anders organisieren

                return params;
            }
        };
    }

    public class DeviceCreationRequest extends StringRequest {

        private static final String URL = "https://teog.virlep.de/devices.php?action=create";

        public DeviceCreationRequest(final Context context, final View disable, final View enable) {
            super(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                    Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_SHORT).show();
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
