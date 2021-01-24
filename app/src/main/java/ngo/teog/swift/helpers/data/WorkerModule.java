package ngo.teog.swift.helpers.data;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ngo.teog.swift.helpers.SynchronizeWorker;

@Module
public interface WorkerModule {
    @ContributesAndroidInjector
    SynchronizeWorker worker();
}
