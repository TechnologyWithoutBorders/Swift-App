package ngo.teog.hstest.comm;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Julian on 24.11.2017.
 */

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;

    public static synchronized VolleySingleton getInstance(Context context) {
        if(instance == null) {
            instance = new VolleySingleton(context);
        }

        return instance;
    }

    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
