package ngo.teog.swift.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;
import ngo.teog.swift.helpers.data.HospitalRepository;

public class SynchronizeWorker extends Worker {

    public static final String TAG = "periodic_background_synchronization";

    @Inject
    HospitalRepository hospitalRepository;

    public SynchronizeWorker(Context context, WorkerParameters params) {
        super(context, params);

        ContextInjection.inject(this, context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(this.getClass().getName(), "synchronize worker running");
        return Result.success();
    }

    static class ContextInjection {
        static void inject(Object target, Context context) {
            ((HasAndroidInjector)context.getApplicationContext()).androidInjector().inject(target);
        }
    }
}
