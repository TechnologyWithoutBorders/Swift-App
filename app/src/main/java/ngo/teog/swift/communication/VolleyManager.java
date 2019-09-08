package ngo.teog.swift.communication;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton, grants access to the Google Volley request queue used for network communication.
 * @author Julian Deyerler
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
