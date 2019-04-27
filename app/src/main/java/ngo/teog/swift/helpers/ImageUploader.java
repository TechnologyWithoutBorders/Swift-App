package ngo.teog.swift.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
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
        return Result.failure();
    }
}
