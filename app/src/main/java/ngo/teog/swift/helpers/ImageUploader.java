package ngo.teog.swift.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.io.File;
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
        String targetName = getInputData().getString(ResourceKeys.PATH);

        if(targetName != null) {
            int deviceId = getInputData().getInt(ResourceKeys.DEVICE_ID, -1);

            File dir = new File(getApplicationContext().getFilesDir(), Defaults.DEVICE_IMAGE_PATH);
            File image = new File(dir, targetName);

            try {
                FileInputStream inputStream = new FileInputStream(image.getPath());

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                VolleyManager volleyManager = VolleyManager.getInstance(this.getApplicationContext());

                RequestQueue queue = volleyManager.getRequestQueue();

                RequestFactory factory = RequestFactory.getInstance();
                JsonObjectRequest request = factory.createDeviceImageUploadRequest(this.getApplicationContext(), deviceId, bitmap);

                queue.add(request);

                return Result.success();
            } catch (Exception e) {
                Data output = new Data.Builder()
                        .putString("ERROR", e.toString())
                        .build();

                return Result.failure(output);
            }
        }

        Data output = new Data.Builder()
                .putString("ERROR", "no input path found")
                .build();

        return Result.failure(output);
    }
}
