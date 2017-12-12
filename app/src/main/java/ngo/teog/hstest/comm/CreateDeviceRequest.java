package ngo.teog.hstest.comm;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request, der über die Volley-Request-Queue an den Server übertragen werden kann.
 * Wird in dieser Form nicht bestehen bleiben, sondern es wird eine Factory-Klasse für Requests geben.
 * Das liegt daran, dass bei GET-Requests die Werte direkt in die URL eingebaut werden müssen. Da aber
 * der erste Aufruf im Konstruktor die super()-Methode sein muss und in dieser bereits die URL übergeben
 * wird, kann man in dieser Form keine Werte hinzufügen (diese können nämlich auch nach dem super()-Aufruf
 * nicht mehr geändert werden)
 * Created by Julian on 24.11.2017.
 */

@Deprecated
public class CreateDeviceRequest extends StringRequest {

    private static final String URL = "https://teog.virlep.de/create_device.php";

    public CreateDeviceRequest(final Context context, final View disable, final View enable) {
        super(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                disable.setVisibility(View.INVISIBLE);
                enable.setVisibility(View.VISIBLE);
                Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                disable.setVisibility(View.INVISIBLE);
                enable.setVisibility(View.VISIBLE);
                Toast.makeText(context.getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected Map<String, String> getParams() {

        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);*/

        Map<String, String> params = new HashMap<>();
        params.put("name", "bla");
        params.put("type", "bla");
        params.put("manufacturer", "bla");
        params.put("serialNumber", "bla");
        params.put("ward", "bla");
        params.put("hospital", "bla");
        params.put("isWorking", "bla");
        params.put("due", "bla");
        params.put("image", "dummy");

        return params;
    }
}
