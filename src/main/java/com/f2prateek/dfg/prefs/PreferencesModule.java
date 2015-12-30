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

package com.f2prateek.dfg.prefs;

import android.graphics.Color;
import com.f2prateek.dfg.prefs.BackgroundColorOption.Option;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(library = true, complete = false) //
public class PreferencesModule {
  @Provides @Singleton @FirstRun //
  Preference<Boolean> provideFirstRun(RxSharedPreferences preferences) {
    return preferences.getBoolean("first_run", true);
  }

  @Provides @Singleton @DefaultDevice //
  Preference<String> provideDefaultDevice(RxSharedPreferences preferences) {
    return preferences.getString("default_device_id", "nexus_5");
  }

  @Provides @Singleton @GlareEnabled //
  Preference<Boolean> provideGlareEnabled(RxSharedPreferences preferences) {
    return preferences.getBoolean("glare_enabled", true);
  }

  @Provides @Singleton @ShadowEnabled //
  Preference<Boolean> provideShadowEnabled(RxSharedPreferences preferences) {
    return preferences.getBoolean("shadow_enabled", true);
  }

  @Provides @Singleton @BlurBackgroundEnabled //
  Preference<Boolean> provideBlurBackgroundEnabled(RxSharedPreferences preferences) {
    return preferences.getBoolean("blur_background_enabled", false);
  }

  @Provides @Singleton @BackgroundBlurRadius Preference<Integer> //
  provideBackgroundBlurRadiusPreference(RxSharedPreferences preferences) {
    return preferences.getInteger("background_blur_radius", 15);
  }

  @Provides @Singleton @ColorBackgroundEnabled Preference<Boolean> //
  provideColorBackgroundEnabled(RxSharedPreferences preferences) {
    return preferences.getBoolean("color_background_enabled", false);
  }

  @Provides @Singleton @BackgroundColorOption Preference<Option> //
  provideBackgroundColorOptionPreference(RxSharedPreferences preferences) {
    return preferences.getEnum("background_color_option", Option.MUTED, Option.class);
  }

  @Provides @Singleton @CustomBackgroundColor Preference<Integer> //
  provideCustomBackgroundColorPreference(RxSharedPreferences preferences) {
    return preferences.getInteger("custom_background_color", Color.DKGRAY);
  }

  @Provides @Singleton @BackgroundPaddingPercentage Preference<Integer> //
  provideBackgroundPaddingPercentagePreference(RxSharedPreferences preferences) {
    return preferences.getInteger("background_padding_percentage", 10);
  }
}
