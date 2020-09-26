package ngo.teog.swift.gui.hospital;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.BaseResponseListener;
import ngo.teog.swift.communication.DataAction;
import ngo.teog.swift.communication.BaseRequest;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.SwiftResponse;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.gui.BaseActivity;
import ngo.teog.swift.helpers.Defaults;

public class StorageAccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_access);

        if(this.checkForInternetConnection()) {
            RequestQueue queue = VolleyManager.getInstance(this).getRequestQueue();

            BaseRequest request = this.createAccessRequest(this, findViewById(R.id.qr_code), findViewById(R.id.storage_access));

            queue.add(request);
        } else {
            Toast.makeText(this, getText(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public BaseRequest createAccessRequest(final Context context, View enable, View disable) {
        final String url = Defaults.BASE_URL + "info.php";

        Map<String, String> params = RequestFactory.generateParameterMap(context, DataAction.ACCESS_STORAGE, true);

        JSONObject request = new JSONObject(params);

        return new BaseRequest(context, url, request, disable, enable, new BaseResponseListener(context, disable, enable) {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                super.onSuccess(response);

                String key = response.getString(SwiftResponse.DATA_FIELD);

                QRCodeWriter writer = new QRCodeWriter();

                try {
                    BitMatrix bitMatrix = writer.encode(key, BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    ((ImageView) findViewById(R.id.qr_code)).setImageBitmap(bmp);

                } catch (WriterException e) {
                    Log.w(this.getClass().getName(), e.toString());
                    Toast.makeText(StorageAccessActivity.this, getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}