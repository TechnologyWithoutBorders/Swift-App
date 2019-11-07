package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;

import java.io.FileInputStream;

import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;

/**
 * Worker used for uploading device images.
 * @author nitelow
 */
public class ImageUploader extends Worker {

    /**
     * Creates a new ImageUploader.
     * @param context Context
     * @param params Worker parameters
     */
    public ImageUploader(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    @NonNull
    public Worker.Result doWork() {
        String imagePath = getInputData().getString(ResourceKeys.PATH);
        int deviceId = getInputData().getInt(ResourceKeys.DEVICE_ID, -1);

        try {
            FileInputStream inputStream = this.getApplicationContext().openFileInput(imagePath);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            VolleyManager volleyManager = VolleyManager.getInstance(this.getApplicationContext());

            RequestQueue queue = volleyManager.getRequestQueue();

            RequestFactory factory =  RequestFactory.getInstance();
            RequestFactory.DeviceImageUploadRequest request = factory.createDeviceImageUploadRequest(this.getApplicationContext(), deviceId, bitmap);

            queue.add(request);

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}
