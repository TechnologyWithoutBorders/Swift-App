package ngo.teog.swift.communication;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton, grants access to the Google Volley request queue used for network communication.
 * @author nitelow
 */
public class VolleyManager {
    private static VolleyManager instance;
    private RequestQueue requestQueue;

    /**
     * Returns an instance of VolleyManager and creates one if necessary.
     * @param context Context
     * @return Instance
     */
    public static synchronized VolleyManager getInstance(Context context) {
        if(instance == null) {
            instance = new VolleyManager(context);
        }

        return instance;
    }

    private VolleyManager(Context context) {
        Log.v(this.getClass().getName(), "setting up Volley request queue");
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Returns request queue for network requests.
     * @return Request queue
     */
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
