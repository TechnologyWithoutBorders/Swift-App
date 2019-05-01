package ngo.teog.swift.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;

import java.io.File;

import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;

public class ImageUploader extends Worker {

    private Context context;

    public ImageUploader(Context context, WorkerParameters params) {
        super(context, params);

        this.context = context;
    }

    @Override
    public Worker.Result doWork() {
        String imagePath = getInputData().getString("path");
        int deviceId = getInputData().getInt("device", -1);

        Bitmap bitmap = decode(imagePath, 640);

        VolleyManager volleyManager = VolleyManager.getInstance(context);

        RequestQueue queue = volleyManager.getRequestQueue();

        RequestFactory factory =  new RequestFactory();
        RequestFactory.DeviceCreationRequest request = factory.createDeviceCreationRequest(context, deviceId, bitmap);

        queue.add(request);

        return Result.success();
    }

    private Bitmap decode(String filePath, int targetW) {
        File image = new File(filePath);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetW);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }
}
