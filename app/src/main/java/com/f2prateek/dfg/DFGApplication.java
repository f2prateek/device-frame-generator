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

import android.app.Application;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.f2prateek.dfg.ln.CrashlyticsLn;
import com.f2prateek.dfg.util.StorageUtils;
import com.f2prateek.ln.DebugLn;
import com.f2prateek.ln.Ln;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.squareup.picasso.Picasso;
import dagger.ObjectGraph;

public class DFGApplication extends Application {

  private ObjectGraph applicationGraph;

  @Override
  public void onCreate() {
    super.onCreate();

    // Perform Injection
    applicationGraph = ObjectGraph.create(Modules.list(this));
    applicationGraph.inject(this);

    Picasso.with(this).setDebugging(BuildConfig.DEBUG);
    GoogleAnalytics.getInstance(this).setDryRun(BuildConfig.DEBUG);

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
  }

  public void inject(Object object) {
    applicationGraph.inject(object);
  }
}