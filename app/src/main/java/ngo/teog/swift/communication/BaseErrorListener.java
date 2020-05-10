package ngo.teog.swift.communication;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ngo.teog.swift.R;

public class BaseErrorListener implements Response.ErrorListener {
    private Context context;
    private View disable;
    private View enable;

    public BaseErrorListener(Context context, @Nullable View disable, @Nullable View enable) {
        this.context = context;
        this.disable = disable;
        this.enable = enable;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(disable != null) {
            disable.setVisibility(View.INVISIBLE);
        }

        if(enable != null) {
            enable.setVisibility(View.VISIBLE);
        }

        Toast.makeText(context.getApplicationContext(), context.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
    }
}
