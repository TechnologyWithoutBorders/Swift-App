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
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

        try {
            FileInputStream inputStream = context.openFileInput(imagePath);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            VolleyManager volleyManager = VolleyManager.getInstance(context);

            RequestQueue queue = volleyManager.getRequestQueue();

            RequestFactory factory =  new RequestFactory();
            RequestFactory.DeviceCreationRequest request = factory.createDeviceCreationRequest(context, deviceId, bitmap);

            queue.add(request);

            return Result.success();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return Result.failure();
        }
    }
}
