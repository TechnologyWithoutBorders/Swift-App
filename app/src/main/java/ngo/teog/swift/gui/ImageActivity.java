package ngo.teog.swift.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;

/**
 * Activity that displays an image delivered via the intent.
 * @author nitelow
 */
public class ImageActivity extends BaseActivity {
    private Bitmap bitmap;
    private int device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image_dialog);

        Intent intent = this.getIntent();
        File image = (File)intent.getSerializableExtra(ResourceKeys.IMAGE);

        device = intent.getIntExtra(ResourceKeys.DEVICE_ID, -1);

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(image));

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        } catch(FileNotFoundException e) {
            Toast.makeText(this.getApplicationContext(), this.getText(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.refresh:
                if(this.checkForInternetConnection()) {
                    //in order to minimize traffic, we request a hash of the image and then decide whether to download the image

                    File dir = new File(getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
                    dir.mkdirs();

                    File image = new File(dir, device + ".jpg");

                    ByteArrayOutputStream baos;

                    byte[] buffer = new byte[(int)image.length()];

                    //compute hash of local image
                    try {
                        InputStream is = new FileInputStream(image);
                        is.read(buffer);
                        is.close();

                        MessageDigest digest = MessageDigest.getInstance("MD5");

                        digest.reset();

                        byte[] result = digest.digest(buffer);

                        StringBuilder sb = new StringBuilder();

                        for (byte b : result) {
                            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                        }

                        RequestFactory.ImageHashRequest request = RequestFactory.getInstance().createImageHashRequest(this, device, sb.toString());

                        VolleyManager.getInstance(this).getRequestQueue().add(request);
                    } catch(FileNotFoundException e1) {
                        Toast.makeText(this.getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                    } catch(NoSuchAlgorithmException | IOException e1) {
                        Toast.makeText(this.getApplicationContext(), getString(R.string.generic_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item, R.string.deviceinfo_activity);//TODO Info anpassen
        }
    }
}
