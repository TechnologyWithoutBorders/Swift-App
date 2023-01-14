package ngo.teog.swift.communication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

//TODO: remove entire class
/**
 * Base for Volley requests that implements easy-to-use constructors supporting frequently used patterns.
 * @author nitelow
 */
public class BaseRequest extends JsonObjectRequest {
    public BaseRequest(Context context, String url, JSONObject request) {
        super(Request.Method.POST, url, request, new BaseResponseListener(context), new BaseErrorListener(context));
    }

    public BaseRequest(Context context, String url, JSONObject request, BaseResponseListener listener) {
        super(Request.Method.POST, url, request, listener, new BaseErrorListener(context));
    }
}
