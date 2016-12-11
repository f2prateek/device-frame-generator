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

import android.content.Context;
import com.f2prateek.dfg.ForApplication;
import com.f2prateek.dfg.ui.activities.BaseActivity;
import com.f2prateek.dfg.ui.activities.MainActivity;
import com.f2prateek.dfg.ui.activities.ReceiverActivity;
import com.f2prateek.dfg.ui.fragments.AboutFragment;
import com.f2prateek.dfg.ui.fragments.DeviceFragment;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        BaseActivity.class, MainActivity.class, ReceiverActivity.class, DeviceFragment.class,
        AboutFragment.class, AboutFragment.class
    },
    complete = false,
    library = true)
public class UiModule {
  @Provides @Singleton AppContainer provideAppContainer() {
    return AppContainer.DEFAULT;
  }

  @Provides @Singleton ActivityHierarchyServer provideActivityHierarchyServer() {
    return ActivityHierarchyServer.NONE;
  }

  @Provides @Singleton Picasso providePicasso(@ForApplication Context app) {
    return new Picasso.Builder(app).build();
  }
}
