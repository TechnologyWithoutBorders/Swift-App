package ngo.teog.swift.communication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ngo.teog.swift.R;

/**
 * Response listener for Volley requests.
 * @author nitelow
 */
public class BaseResponseListener implements Response.Listener<JSONObject> {
    private final Context context;

    public BaseResponseListener(Context context) {
        this.context = context;
    }

    /**
     * Called when a response has been received. Checks the response code and displays corresponding error messages if necessary.
     * @param response Response
     */
    @Override
    public void onResponse(JSONObject response) {
        Log.i(this.getClass().getName(), "received response");

        try {
            Log.d(this.getClass().getName(), response.toString(4));
        } catch(JSONException e) {
            Log.d(this.getClass().getName(), "response is corrupted");
        }

        try {
            int responseCode = response.getInt(SwiftResponse.CODE_FIELD);
            switch(responseCode) {
                case SwiftResponse.CODE_OK:
                    onSuccess(response);

                    break;
                case SwiftResponse.CODE_FAILED_VISIBLE:
                    throw new TransparentServerException(response.getString(SwiftResponse.DATA_FIELD));
                case SwiftResponse.CODE_FAILED_HIDDEN:
                default:
                    throw new Exception(response.getString(SwiftResponse.DATA_FIELD));
            }
        } catch(TransparentServerException e) {
            Log.i(this.getClass().getName(), e.toString());
            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Log.w(this.getClass().getName(), e.toString());
            Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when a response containing the correct response code has been received.
     * @param response JSON formatted payload
     * @throws JSONException if parsing the response fails for some reason
     */
    public void onSuccess(JSONObject response) throws JSONException {}
}
