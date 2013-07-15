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

import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Module for all Android related provisions */
@Module(
    complete = false
)
public class AndroidModule {

  @Provides @Singleton Context provideAppContext() {
    return DFGApplication.getInstance().getApplicationContext();
  }

  @Provides SharedPreferences provideDefaultSharedPreferences(final Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Provides PackageInfo providePackageInfo(Context context) {
    try {
      return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides TelephonyManager provideTelephonyManager(Context context) {
    return getSystemService(context, Context.TELEPHONY_SERVICE);
  }

  @SuppressWarnings("unchecked")
  public <T> T getSystemService(Context context, String serviceConstant) {
    return (T) context.getSystemService(serviceConstant);
  }

  @Provides InputMethodManager provideInputMethodManager(final Context context) {
    return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
  }

  @Provides ApplicationInfo provideApplicationInfo(final Context context) {
    return context.getApplicationInfo();
  }

  @Provides AccountManager provideAccountManager(final Context context) {
    return AccountManager.get(context);
  }

  @Provides ClassLoader provideClassLoader(final Context context) {
    return context.getClassLoader();
  }

  @Provides NotificationManager provideNotificationManager(final Context context) {
    return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }
}