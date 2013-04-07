package com.f2prateek.dfg;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.Toast;
import com.f2prateek.dfg.util.StorageUtils;
import dagger.ObjectGraph;

/**
 * Android Bootstrap application
 */
public class DFGApplication extends Application {

    private static DFGApplication instance;
    ObjectGraph objectGraph;

    /**
     * Create main application
     */
    public DFGApplication() {
    }

    /**
     * Create main application
     *
     * @param context Attach a context
     */
    public DFGApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    /**
     * Create main application
     *
     * @param instrumentation Instrumentation to attach
     */
    public DFGApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static DFGApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!StorageUtils.isStorageAvailable()) {
            Toast.makeText(this, R.string.storage_unavailable, Toast.LENGTH_SHORT).show();
        }

        instance = this;
        // Perform Injection
        objectGraph = ObjectGraph.create(getRootModule());
        objectGraph.inject(this);
        objectGraph.injectStatics();

    }

    private Object getRootModule() {
        return new RootModule();
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}