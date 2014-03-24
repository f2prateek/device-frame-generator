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

import android.content.SharedPreferences;
import com.f2prateek.dfg.prefs.BooleanPreference;
import com.f2prateek.dfg.prefs.DefaultDevice;
import com.f2prateek.dfg.prefs.GlareEnabled;
import com.f2prateek.dfg.prefs.ShadowEnabled;
import com.f2prateek.dfg.prefs.StringPreference;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(library = true, complete = false)
public class PreferencesModule {
  private static final String KEY_PREF_DEFAULT_DEVICE_ID = "KEY_PREF_DEFAULT_DEVICE_ID";
  private static final String KEY_PREF_OPTION_GLARE = "KEY_PREF_OPTION_GLARE";
  private static final String KEY_PREF_OPTION_SHADOW = "KEY_PREF_OPTION_SHADOW";

  @Provides @Singleton @DefaultDevice
  StringPreference provideDefaultDevice(SharedPreferences sharedPreferences) {
    return new StringPreference(sharedPreferences, KEY_PREF_DEFAULT_DEVICE_ID, "nexus_5");
  }

  @Provides @Singleton @GlareEnabled
  BooleanPreference provideGlareEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_GLARE, true);
  }

  @Provides @Singleton @ShadowEnabled
  BooleanPreference provideShadowEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_SHADOW, true);
  }
}
