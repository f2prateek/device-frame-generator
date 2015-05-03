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
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import rx.android.preferences.BooleanPreference;
import rx.android.preferences.EnumPreference;
import rx.android.preferences.IntPreference;
import rx.android.preferences.StringPreference;

@Module(library = true, complete = false)
public class PreferencesModule {
  private static final String DEFAULT_DEVICE_ID = "nexus_5"; // Nexus 5
  private static final boolean DEFAULT_GLARE_ENABLED = true; // Glare drawn
  private static final boolean DEFAULT_SHADOW_ENABLED = true; // Shadow drawn
  private static final int DEFAULT_BACKGROUND_PADDING_PERCENTAGE = 10; // 10% of screenshot size
  private static final int DEFAULT_BACKGROUND_BLUR_RADIUS = 15;

  @Provides @Singleton @FirstRun //
  BooleanPreference provideFirstRun(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "first_run", true);
  }

  @Provides @Singleton @DefaultDevice //
  StringPreference provideDefaultDevice(SharedPreferences sharedPreferences) {
    return new StringPreference(sharedPreferences, "default_device_id", DEFAULT_DEVICE_ID);
  }

  @Provides @Singleton @GlareEnabled //
  BooleanPreference provideGlareEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "glare_enabled", DEFAULT_GLARE_ENABLED);
  }

  @Provides @Singleton @ShadowEnabled //
  BooleanPreference provideShadowEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "shadow_enabled", DEFAULT_SHADOW_ENABLED);
  }

  /** Mutually exclusive with {@link ColorBackgroundEnabled}. */
  @Provides @Singleton @BlurBackgroundEnabled //
  BooleanPreference provideBlurBackgroundEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "blur_background_enabled", false);
  }

  /** Hidden from the UI, not controllable by the user. */
  @Provides @Singleton @BackgroundBlurRadius IntPreference //
  provideBackgroundBlurRadiusPreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, "background_blur_radius",
        DEFAULT_BACKGROUND_BLUR_RADIUS);
  }

  /** Mutually exclusive with {@link BlurBackgroundEnabled}. */
  @Provides @Singleton @ColorBackgroundEnabled //
  BooleanPreference provideColorBackgroundEnabled(SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "color_background_enabled", false);
  }

  /** Hidden from the UI, not controllable by the user. */
  @Provides @Singleton @BackgroundColorOption EnumPreference<BackgroundColorOption.Option> //
  provideBackgroundColorOptionPreference(SharedPreferences sharedPreferences) {
    return new EnumPreference<>(sharedPreferences, BackgroundColorOption.Option.class,
        "background_color_option", BackgroundColorOption.Option.MUTED);
  }

  /** Hidden from the UI, not controllable by the user. */
  @Provides @Singleton @CustomBackgroundColor IntPreference //
  provideCustomBackgroundColorPreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, "custom_background_color", Color.DKGRAY);
  }

  /** Hidden from the UI, not controllable by the user. */
  @Provides @Singleton @BackgroundPaddingPercentage IntPreference //
  provideBackgroundPaddingPercentagePreference(SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, "background_padding_percentage",
        DEFAULT_BACKGROUND_PADDING_PERCENTAGE);
  }
}
