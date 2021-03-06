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
    private View disable = null;
    private View enable = null;

    public BaseErrorListener(Context context) {
        this.context = context;
    }

    /**
     * Returns a new BaseErrorListener.
     * @param context Context
     * @param disableOnFinish View that should be hidden after the request has failed.
     * @param enableOnFinish View that should be visible after the request has failed.
     */
    public BaseErrorListener(Context context, @Nullable View disableOnFinish, @Nullable View enableOnFinish) {
        this.context = context;
        this.disable = disableOnFinish;
        this.enable = enableOnFinish;
    }

    /**
     * Called when a Volley request fails, manages view visibilities and displays a generic error message.
     * @param error Error that has occurred
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        if(disable != null) {
            disable.setVisibility(View.INVISIBLE);
        }

        if(enable != null) {
            enable.setVisibility(View.VISIBLE);
        }

        Log.w(this.getClass().getName(), error.toString());
        Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
    }
}
