/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.f2prateek.dfg.model.Bounds;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.prefs.DefaultDevice;
import com.f2prateek.dfg.prefs.model.StringPreference;
import com.f2prateek.dfg.ui.ActivityHierarchyServer;
import com.f2prateek.dfg.util.StorageUtils;
import com.f2prateek.ln.DebugLn;
import com.f2prateek.ln.Ln;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import java.util.Map;
import javax.inject.Inject;

public class DFGApplication extends Application {

  ObjectGraph applicationGraph;
  @Inject ActivityHierarchyServer activityHierarchyServer;
  @Inject Bus bus;

  @Inject WindowManager windowManager;
  @Inject Map<String, Device> deviceMap;
  @Inject @DefaultDevice StringPreference defaultDevice;

  @Override
  public void onCreate() {
    super.onCreate();

    // Perform Injection
    buildApplicationGraphAndInject();

    registerActivityLifecycleCallbacks(activityHierarchyServer);

    if (BuildConfig.DEBUG) {
      Ln.set(DebugLn.from(this));
    } else {
      Crashlytics.start(this);
      Ln.set(new CrashlyticsLn(getPackageName()));
    }

    if (!StorageUtils.isStorageAvailable()) {
      Toast.makeText(this, R.string.storage_unavailable, Toast.LENGTH_SHORT).show();
      Ln.w("storage unavailable");
    }

    setupDefaultDevice();
  }

  /**
   * Setup the default device for the user. Skips if the devicePreference has already been set.
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void setupDefaultDevice() {
    if (defaultDevice.isSet()) {
      // skip if device has been set
      return;
    }

    // Explicitly it to a default value so we don't run this everytime the app starts
    defaultDevice.set(defaultDevice.get());

    // look by {@link android.os.Build#PRODUCT} value
    String id = huntDeviceIdByProduct();
    if (id != null) {
      defaultDevice.set(id);
      bus.post(new Events.DefaultDeviceUpdated(deviceMap.get(id)));
      return;
    }

    // Could find by product name, so not look by dimensions
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      // The following API is only on 17+ so skip it if this is not available.
      return;
    }
    DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getRealMetrics(metrics);
    Bounds bounds = Bounds.create(metrics.heightPixels, metrics.widthPixels);
    Ln.d("Searching for a device with bounds %s", bounds);
    for (Device device : deviceMap.values()) {
      if (Orientation.calculate(bounds, device) != null) {
        defaultDevice.set(device.id());
        bus.post(new Events.DefaultDeviceUpdated(device));
      }
    }
  }

  /**
   * Ideally we would have devices id'd by product name (e.g. crespo instead of nexus_s)
   * However different flavours (e.g. crespo and crespo4g) are going to require manual checking
   * anyway.
   *
   * @return Device id matching matching our device map.
   */
  private String huntDeviceIdByProduct() {
    String product = Build.PRODUCT;
    for (Device device : deviceMap.values()) {
      if (device.productIds().contains(product)) {
        Ln.d("Found a match for Build.PRODUCT %s", device);
        return device.id();
      }
    }
    return null;
  }

  @DebugLog
  public void buildApplicationGraphAndInject() {
    applicationGraph = ObjectGraph.create(Modules.list(this));
    applicationGraph.inject(this);
  }

  public static DFGApplication get(Context context) {
    return (DFGApplication) context.getApplicationContext();
  }

  public void inject(Object object) {
    applicationGraph.inject(object);
  }
}