package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;

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
    @NonNull
    public Worker.Result doWork() {
        String imagePath = getInputData().getString(ResourceKeys.PATH);
        int deviceId = getInputData().getInt(ResourceKeys.DEVICE_ID, -1);

        try {
            FileInputStream inputStream = context.openFileInput(imagePath);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            VolleyManager volleyManager = VolleyManager.getInstance(context);

            RequestQueue queue = volleyManager.getRequestQueue();

            RequestFactory factory =  RequestFactory.getInstance();
            RequestFactory.DeviceImageUploadRequest request = factory.createDeviceImageUploadRequest(context, deviceId, bitmap);

            queue.add(request);

            Log.d("IMAGE_UPLOAD", "request created");

            return Result.success();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return Result.failure();
        }
    }
}
