package ngo.teog.swift.communication;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class DefaultRequest extends JsonObjectRequest {
    public DefaultRequest(Context context, String url, JSONObject request, @Nullable View disable, @Nullable View enable, BaseResponseListener listener) {
        super(Request.Method.POST, url, request, listener, new BaseErrorListener(context, disable, enable));
    }
}
