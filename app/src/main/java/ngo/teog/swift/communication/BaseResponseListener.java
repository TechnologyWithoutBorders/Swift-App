package ngo.teog.swift.communication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONObject;

import ngo.teog.swift.R;

public abstract class BaseResponseListener implements Response.Listener<JSONObject> {
    private Context context;
    private View disable;
    private View enable;

    public BaseResponseListener(Context context, @Nullable View disable, @Nullable View enable) {
        this.context = context;
        this.disable = disable;
        this.enable = enable;
    }

    @Override
    public void onResponse(JSONObject response) {
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

        if(disable != null) {
            disable.setVisibility(View.INVISIBLE);
        }

        if(enable != null) {
            enable.setVisibility(View.VISIBLE);
        }
    }

    public abstract void onSuccess(JSONObject response) throws Exception;
}
