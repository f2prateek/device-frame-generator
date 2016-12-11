/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/** A "view server" adaptation which automatically hooks itself up to all activities. */
public interface ActivityHierarchyServer extends Application.ActivityLifecycleCallbacks {
  /** An {@link ActivityHierarchyServer} which does nothing. */
  ActivityHierarchyServer NONE = new ActivityHierarchyServer() {
    @Override public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override public void onActivityStarted(Activity activity) {
    }

    @Override public void onActivityResumed(Activity activity) {
    }

    @Override public void onActivityPaused(Activity activity) {
    }

    @Override public void onActivityStopped(Activity activity) {
    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override public void onActivityDestroyed(Activity activity) {
    }
  };
}
