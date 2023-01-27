package ngo.teog.swift.communication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ngo.teog.swift.R;

/**
 * Error listener that is called when a Volley request fails.
 * @author nitelow
 */
public class BaseErrorListener implements Response.ErrorListener {
    private final Context context;

    public BaseErrorListener(Context context) {
        this.context = context;
    }

    /**
     * Called when a Volley request fails, manages view visibilities and displays a generic error message.
     * @param error Error that has occurred
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.w(this.getClass().getName(), error.toString());
        Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
    }
}
