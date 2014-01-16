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

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Module for all Android related provisions */
@Module(
    library = true,
    complete = false)
public class AndroidModule {

  private final DFGApplication application;

  public AndroidModule(DFGApplication application) {
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
  public <T> T getSystemService(final Context context, final String serviceConstant) {
    return (T) context.getSystemService(serviceConstant);
  }

  @Provides @Singleton NotificationManager provideNotificationManager(final Context context) {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }
}