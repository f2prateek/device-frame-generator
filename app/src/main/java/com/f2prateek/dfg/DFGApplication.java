/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import dagger.ObjectGraph;

/** Android Bootstrap application */
public class DFGApplication extends Application {

  private static DFGApplication instance;
  ObjectGraph objectGraph;

  /** Create main application */
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

    if (!StorageUtils.isStorageAvailable()) {
      Toast.makeText(this, R.string.storage_unavailable, Toast.LENGTH_SHORT).show();
    }
  }

  private Object getRootModule() {
    return new RootModule();
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }
}