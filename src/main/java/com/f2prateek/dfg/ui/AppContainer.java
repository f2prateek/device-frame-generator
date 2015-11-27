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
import android.view.ViewGroup;
import com.f2prateek.dfg.DFGApplication;

import static butterknife.ButterKnife.findById;

/** An indirection which allows controlling the root container used for each activity. */
public interface AppContainer {
  /** The root {@link android.view.ViewGroup} into which the activity should place its contents. */
  ViewGroup get(Activity activity, DFGApplication app);

  /** An {@link AppContainer} which returns the normal activity content view. */
  AppContainer DEFAULT = new AppContainer() {
    @Override public ViewGroup get(Activity activity, DFGApplication app) {
      return findById(activity, android.R.id.content);
    }
  };
}
