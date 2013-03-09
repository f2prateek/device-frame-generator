
package com.f2prateek.dfg.ui;

import android.content.Context;
import android.util.Log;

import roboguice.util.Ln;

/**
 * Loader that support throwing an exception when loading in the background
 *
 * @param <D>
 */
public abstract class ThrowableLoader<D> extends AsyncLoader<D> {


    private final D data;

    private Exception exception;

    /**
     * Create loader for context and seeded with initial data
     *
     * @param context
     * @param data
     */
    public ThrowableLoader(Context context, D data) {
        super(context);

        this.data = data;
    }

    @Override
    public D loadInBackground() {
        exception = null;
        try {
            return loadData();
        } catch (Exception e) {
            Ln.d(e, "Exception loading data");
            exception = e;
            return data;
        }
    }

    /**
     * @return exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Clear the stored exception and return it
     *
     * @return exception
     */
    public Exception clearException() {
        final Exception throwable = exception;
        exception = null;
        return throwable;
    }

    /**
     * Load data
     *
     * @return data
     * @throws Exception
     */
    public abstract D loadData() throws Exception;
}
