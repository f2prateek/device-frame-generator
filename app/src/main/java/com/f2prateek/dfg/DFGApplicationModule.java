/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.f2prateek.dfg;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.ui.AboutFragment;
import com.f2prateek.dfg.ui.BaseActivity;
import com.f2prateek.dfg.ui.DeviceFragment;
import com.f2prateek.dfg.ui.MainActivity;
import com.f2prateek.dfg.ui.ReceiverActivity;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    includes = {DeviceModule.class, PreferencesModule.class, DeviceModule.class},
    injects = {
        DFGApplication.class, BaseActivity.class, MainActivity.class, ReceiverActivity.class,
        DeviceFragment.class, AboutFragment.class, AbstractGenerateFrameService.class,
        GenerateFrameService.class, GenerateMultipleFramesService.class, AboutFragment.class
    }
)
public class DFGApplicationModule {

  private final DFGApplication application;

  public DFGApplicationModule(DFGApplication application) {
    this.application = application;
  }

  @Provides @Singleton Context provideAppContext() {
    return application;
  }

  @Provides @Singleton SharedPreferences provideDefaultSharedPreferences(final Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Provides @Singleton PackageInfo providePackageInfo(final Context context) {
    try {
      return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  <T> T getSystemService(final Context context, final String serviceConstant) {
    return (T) context.getSystemService(serviceConstant);
  }

  @Provides @Singleton NotificationManager provideNotificationManager(final Context context) {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @Provides @Singleton Bus provideOttoBus() {
    return new Bus();
  }
}