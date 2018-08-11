package ngo.teog.swift.communication;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
