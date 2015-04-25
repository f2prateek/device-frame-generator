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

import android.content.SharedPreferences;
import android.graphics.Color;
import com.f2prateek.dfg.prefs.model.BooleanPreference;
import com.f2prateek.dfg.prefs.model.EnumPreference;
import com.f2prateek.dfg.prefs.model.IntPreference;
import com.f2prateek.dfg.prefs.model.StringPreference;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(library = true, complete = false)
public class PreferencesModule {
  private static final String DEFAULT_DEVICE_ID = "nexus_5"; // Nexus 5
  private static final boolean DEFAULT_GLARE_ENABLED = true; // Glare drawn
  private static final boolean DEFAULT_SHADOW_ENABLED = true; // Shadow drawn
  private static final int DEFAULT_BACKGROUND_PADDING_PERCENTAGE = 10; // 10% of screenshot size
  private static final int DEFAULT_BACKGROUND_BLUR_RADIUS = 15;

  private static final String KEY_FIRST_RUN = "KEY_FIRST_RUN";
  private static final String KEY_PREF_DEFAULT_DEVICE_ID = "KEY_PREF_DEFAULT_DEVICE_ID";
  private static final String KEY_PREF_OPTION_GLARE = "KEY_PREF_OPTION_GLARE";
  private static final String KEY_PREF_OPTION_SHADOW = "KEY_PREF_OPTION_SHADOW";
  private static final String KEY_PREF_OPTION_BLUR_BACKGROUND = "KEY_PREF_OPTION_BLUR_BACKGROUND";
  private static final String KEY_PREF_OPTION_COLOR_BACKGROUND = "KEY_PREF_OPTION_COLOR_BACKGROUND";
  private static final String KEY_PREF_OPTION_BACKGROUND_COLOR = "KEY_PREF_OPTION_BACKGROUND_COLOR";
  private static final String KEY_PREF_OPTION_CUSTOM_BACKGROUND_COLOR =
      "KEY_PREF_OPTION_CUSTOM_BACKGROUND_COLOR";
  private static final String KEY_PREF_BACKGROUND_PADDING_PERCENTAGE =
      "KEY_PREF_OPTION_BACKGROUND_PADDING_PERCENTAGE";
  private static final String KEY_PREF_BACKGROUND_BLUR_RADIUS =
      "KEY_PREF_OPTION_BACKGROUND_BLUR_RADIUS";

  @Provides @Singleton @FirstRun //
  BooleanPreference provideFirstRun(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_FIRST_RUN, true);
  }

  @Provides @Singleton @DefaultDevice //
  StringPreference provideDefaultDevice(SharedPreferences sharedPreferences) {
    return new StringPreference(sharedPreferences, KEY_PREF_DEFAULT_DEVICE_ID, DEFAULT_DEVICE_ID);
  }

  @Provides @Singleton @GlareEnabled //
  BooleanPreference provideGlareEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_GLARE, DEFAULT_GLARE_ENABLED);
  }

  @Provides @Singleton @ShadowEnabled //
  BooleanPreference provideShadowEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_SHADOW, DEFAULT_SHADOW_ENABLED);
  }

  @Provides @Singleton @BlurBackgroundEnabled //
  BooleanPreference provideBlurBackgroundEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_BLUR_BACKGROUND, false);
  }

  @Provides @Singleton @ColorBackgroundEnabled //
  BooleanPreference provideColorBackgroundEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, KEY_PREF_OPTION_COLOR_BACKGROUND, false);
  }

  @Provides @Singleton @BackgroundColor EnumPreference<BackgroundColor.Option> //
  provideBackgroundColorOptionPreference(SharedPreferences sharedPreferences) {
    return new EnumPreference<>(sharedPreferences, KEY_PREF_OPTION_BACKGROUND_COLOR,
        BackgroundColor.Option.CUSTOM, BackgroundColor.Option.class);
  }

  @Provides @Singleton @CustomBackgroundColor IntPreference //
  provideCustomBackgroundColorPreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, KEY_PREF_OPTION_CUSTOM_BACKGROUND_COLOR,
        Color.DKGRAY);
  }

  @Provides @Singleton @BackgroundPaddingPercentage IntPreference //
  provideBackgroundPaddingPercentagePreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, KEY_PREF_BACKGROUND_PADDING_PERCENTAGE,
        DEFAULT_BACKGROUND_PADDING_PERCENTAGE);
  }

  @Provides @Singleton @BackgroundBlurRadius IntPreference //
  provideBackgroundBlurRadiusPreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, KEY_PREF_BACKGROUND_BLUR_RADIUS,
        DEFAULT_BACKGROUND_BLUR_RADIUS);
  }
}
