package ngo.teog.hstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class NewDeviceActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Bitmap bitmap;
    private Button createButton;
    private ProgressBar progressBar;

    public final static char CR  = (char) 0x0D;
    public final static char LF  = (char) 0x0A;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_device);

        imageView = findViewById(R.id.imageView);

        if(savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("IMAGE");
            imageView.setImageBitmap(bitmap);
        }

        createButton = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        outState.putParcelable("IMAGE", bitmap);
        super.onSaveInstanceState(outState);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void createDevice(View view) {
        new CreateTask().execute(null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap)extras.get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    private class CreateTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected void onPreExecute() {
            createButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            try {
                URL url = new URL("https://teog.virlep.de/create_device.php");
                URLConnection urlConn = url.openConnection();

                HttpsURLConnection httpsConn = (HttpsURLConnection)urlConn;
                httpsConn.setDoOutput(true);
                httpsConn.setDoInput(true);
                httpsConn.setRequestMethod("POST");

                httpsConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                httpsConn.connect();

                OutputStream out = httpsConn.getOutputStream();

                out.write(("name=bla&type=bla&manufacturer=bla&serialNumber=bla&ward=bla&hospital=bla&isWorking=true&due=0000-00-00&image=bla").getBytes("UTF-8"));

                // Send a binary file
                /*out.writeBytes("Content-Disposition: form-data; name=\"param3\";filename=\"test_file.dat\"" + lineEnd);
                out.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                out.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                out.writeBytes(lineEnd);

                int size = bitmap.getRowBytes() * bitmap.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                bitmap.copyPixelsToBuffer(byteBuffer);

                out.write(byteBuffer.array());
                out.writeBytes(lineEnd);*/

                out.flush();

                String result = "";

                InputStream is = httpsConn.getInputStream();
                byte[] b = new byte[1024];
                while ( is.read(b) != -1)
                    result += new String(b);

                httpsConn.disconnect();

                return result;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            createButton.setVisibility(View.VISIBLE);

            /*if(result != null) {
                Intent intent = new Intent(NewDeviceActivity.this, DeviceInfoActivity.class);
                intent.putExtra("device", result);
                startActivity(intent);
            } else {*/
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            //}
        }
    }
}
