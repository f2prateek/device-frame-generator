package com.f2prateek.dfg;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
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

        instance = this;
        // Perform Injection
        objectGraph = ObjectGraph.create(getRootModule());
        objectGraph.inject(this);
        objectGraph.injectStatics();

        BugSenseHandler.initAndStartSession(this, AppConstants.BUG_SENSE_API_KEY);

        if (!StorageUtils.isStorageAvailable()) {
            Toast.makeText(this, R.string.storage_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTerminate() {
        BugSenseHandler.closeSession(DFGApplication.this);
        super.onTerminate();
    }

    private Object getRootModule() {
        return new RootModule();
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}