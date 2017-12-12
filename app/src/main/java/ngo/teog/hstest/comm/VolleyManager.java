package ngo.teog.hstest.comm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Schnittstelle für die Volley-Request-Queue. Ist ein Singleton, weil es pro Anwendung
 * nur eine Request-Queue geben soll.
 * Created by Julian on 24.11.2017.
 */

public class VolleyManager {
    private static VolleyManager instance;
    private RequestQueue requestQueue;

    public static synchronized VolleyManager getInstance(Context context) {
        if(instance == null) {
            instance = new VolleyManager(context);
        }

        return instance;
    }

    private VolleyManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
