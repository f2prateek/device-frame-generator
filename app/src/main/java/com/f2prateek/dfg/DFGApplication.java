/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.Toast;
import com.f2prateek.dfg.util.StorageUtils;
import com.google.inject.Injector;
import com.google.inject.Stage;
import roboguice.RoboGuice;

/**
 * Device Frame Generator application
 */
public class DFGApplication extends Application {

    public DFGApplication() {
    }

    public DFGApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    public DFGApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    /**
     * Sets the application injector. Using the {@link RoboGuice#newDefaultRoboModule} as well as a
     * custom binding module {@link DFGModule} to set up your application module
     *
     * @param application
     * @return
     */
    public static Injector setApplicationInjector(Application application) {
        return RoboGuice.setBaseApplicationInjector(application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule
                (application), new DFGModule());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setApplicationInjector(this);
        if (!StorageUtils.checkStorageAvailable()) {
            Toast.makeText(this, "Storage unavilable", Toast.LENGTH_SHORT).show();
        }
    }
}
