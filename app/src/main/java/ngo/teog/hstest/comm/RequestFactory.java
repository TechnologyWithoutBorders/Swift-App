package ngo.teog.hstest.comm;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

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
}
