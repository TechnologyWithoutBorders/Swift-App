package ngo.teog.swift.communication;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * @author nitelow
 */
public class BaseRequest extends JsonObjectRequest {
    public BaseRequest(Context context, String url, JSONObject request, BaseResponseListener listener) {
        super(Request.Method.POST, url, request, listener, new BaseErrorListener(context));
    }

    public BaseRequest(Context context, String url, JSONObject request, @Nullable View disableOnFinish, @Nullable View enableOnFinish, BaseResponseListener listener) {
        super(Request.Method.POST, url, request, listener, new BaseErrorListener(context, disableOnFinish, enableOnFinish));
    }
}
