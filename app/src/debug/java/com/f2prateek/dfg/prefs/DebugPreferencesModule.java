package com.f2prateek.dfg.prefs;

import android.content.SharedPreferences;
import com.f2prateek.dfg.prefs.debug.AnimationSpeed;
import com.f2prateek.dfg.prefs.debug.PicassoDebugging;
import com.f2prateek.dfg.prefs.debug.PixelGridEnabled;
import com.f2prateek.dfg.prefs.debug.PixelRatioEnabled;
import com.f2prateek.dfg.prefs.debug.ScalpelEnabled;
import com.f2prateek.dfg.prefs.debug.ScalpelWireframeEnabled;
import com.f2prateek.dfg.prefs.debug.SeenDebugDrawer;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(complete = false, library = true)
public class DebugPreferencesModule {
  private static final int DEFAULT_ANIMATION_SPEED = 1; // 1x (normal) speed.
  private static final boolean DEFAULT_PICASSO_DEBUGGING = false; // Debug indicators displayed
  private static final boolean DEFAULT_PIXEL_GRID_ENABLED = false; // No pixel grid overlay.
  private static final boolean DEFAULT_PIXEL_RATIO_ENABLED = false; // No pixel ratio overlay.
  private static final boolean DEFAULT_SCALPEL_ENABLED = false; // No crazy 3D view tree.
  private static final boolean DEFAULT_SCALPEL_WIREFRAME_ENABLED = false; // Draw views by default.
  private static final boolean DEFAULT_SEEN_DEBUG_DRAWER = false; // Show debug drawer first time.

  @Provides @Singleton @AnimationSpeed IntPreference provideAnimationSpeed(
      SharedPreferences preferences) {
    return new IntPreference(preferences, "debug_animation_speed", DEFAULT_ANIMATION_SPEED);
  }

  @Provides @Singleton @PicassoDebugging BooleanPreference providePicassoDebugging(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_picasso_debugging", DEFAULT_PICASSO_DEBUGGING);
  }

  @Provides @Singleton @PixelGridEnabled BooleanPreference providePixelGridEnabled(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_pixel_grid_enabled",
        DEFAULT_PIXEL_GRID_ENABLED);
  }

  @Provides @Singleton @PixelRatioEnabled BooleanPreference providePixelRatioEnabled(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_pixel_ratio_enabled",
        DEFAULT_PIXEL_RATIO_ENABLED);
  }

  @Provides @Singleton @SeenDebugDrawer BooleanPreference provideSeenDebugDrawer(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_seen_debug_drawer", DEFAULT_SEEN_DEBUG_DRAWER);
  }

  @Provides @Singleton @ScalpelEnabled BooleanPreference provideScalpelEnabled(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_scalpel_enabled", DEFAULT_SCALPEL_ENABLED);
  }

  @Provides @Singleton @ScalpelWireframeEnabled BooleanPreference provideScalpelWireframeEnabled(
      SharedPreferences preferences) {
    return new BooleanPreference(preferences, "debug_scalpel_wireframe_drawer",
        DEFAULT_SCALPEL_WIREFRAME_ENABLED);
  }
}
