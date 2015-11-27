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

package com.f2prateek.dfg.ui.debug;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.f2prateek.dfg.BuildConfig;
import com.f2prateek.dfg.DFGApplication;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.Utils;
import com.f2prateek.dfg.prefs.debug.AnimationSpeed;
import com.f2prateek.dfg.prefs.debug.PicassoDebugging;
import com.f2prateek.dfg.prefs.debug.PixelGridEnabled;
import com.f2prateek.dfg.prefs.debug.PixelRatioEnabled;
import com.f2prateek.dfg.prefs.debug.ScalpelEnabled;
import com.f2prateek.dfg.prefs.debug.ScalpelWireframeEnabled;
import com.f2prateek.dfg.prefs.debug.SeenDebugDrawer;
import com.f2prateek.dfg.ui.AppContainer;
import com.f2prateek.ln.Ln;
import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.madge.MadgeFrameLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;

import static butterknife.ButterKnife.findById;

/**
 * An {@link com.f2prateek.dfg.ui.AppContainer} for debug builds which wrap the content view with a
 * sliding drawer on the right that holds all of the debug information and settings.
 */
@Singleton public class DebugAppContainer implements AppContainer {
  private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

  private final Picasso picasso;
  private final Preference<Integer> animationSpeed;
  private final Preference<Boolean> picassoDebugging;
  private final Preference<Boolean> pixelGridEnabled;
  private final Preference<Boolean> pixelRatioEnabled;
  private final Preference<Boolean> scalpelEnabled;
  private final Preference<Boolean> scalpelWireframeEnabled;
  private final Preference<Boolean> seenDebugDrawer;

  DFGApplication app;
  Activity activity;
  Context drawerContext;

  @Inject
  public DebugAppContainer(Picasso picasso, @AnimationSpeed Preference<Integer> animationSpeed,
      @PicassoDebugging Preference<Boolean> picassoDebugging,
      @PixelGridEnabled Preference<Boolean> pixelGridEnabled,
      @PixelRatioEnabled Preference<Boolean> pixelRatioEnabled,
      @ScalpelEnabled Preference<Boolean> scalpelEnabled,
      @ScalpelWireframeEnabled Preference<Boolean> scalpelWireframeEnabled,
      @SeenDebugDrawer Preference<Boolean> seenDebugDrawer) {
    this.picasso = picasso;
    this.scalpelEnabled = scalpelEnabled;
    this.scalpelWireframeEnabled = scalpelWireframeEnabled;
    this.seenDebugDrawer = seenDebugDrawer;
    this.animationSpeed = animationSpeed;
    this.picassoDebugging = picassoDebugging;
    this.pixelGridEnabled = pixelGridEnabled;
    this.pixelRatioEnabled = pixelRatioEnabled;
  }

  @InjectView(R.id.debug_drawer_layout) DrawerLayout drawerLayout;
  @InjectView(R.id.debug_content) ViewGroup content;

  @InjectView(R.id.madge_container) MadgeFrameLayout madgeFrameLayout;
  @InjectView(R.id.debug_content) ScalpelFrameLayout scalpelFrameLayout;

  @InjectView(R.id.debug_contextual_title) View contextualTitleView;
  @InjectView(R.id.debug_contextual_list) LinearLayout contextualListView;

  @InjectView(R.id.debug_ui_animation_speed) Spinner uiAnimationSpeedView;
  @InjectView(R.id.debug_ui_pixel_grid) Switch uiPixelGridView;
  @InjectView(R.id.debug_ui_pixel_ratio) Switch uiPixelRatioView;
  @InjectView(R.id.debug_ui_scalpel) Switch uiScalpelView;
  @InjectView(R.id.debug_ui_scalpel_wireframe) Switch uiScalpelWireframeView;

  @InjectView(R.id.debug_build_name) TextView buildNameView;
  @InjectView(R.id.debug_build_code) TextView buildCodeView;
  @InjectView(R.id.debug_build_sha) TextView buildShaView;
  @InjectView(R.id.debug_build_date) TextView buildDateView;

  @InjectView(R.id.debug_device_make) TextView deviceMakeView;
  @InjectView(R.id.debug_device_model) TextView deviceModelView;
  @InjectView(R.id.debug_device_product) TextView deviceProductView;
  @InjectView(R.id.debug_device_resolution) TextView deviceResolutionView;
  @InjectView(R.id.debug_device_density) TextView deviceDensityView;
  @InjectView(R.id.debug_device_release) TextView deviceReleaseView;
  @InjectView(R.id.debug_device_api) TextView deviceApiView;

  @InjectView(R.id.debug_picasso_indicators) Switch picassoIndicatorView;
  @InjectView(R.id.debug_picasso_cache_size) TextView picassoCacheSizeView;
  @InjectView(R.id.debug_picasso_cache_hit) TextView picassoCacheHitView;
  @InjectView(R.id.debug_picasso_cache_miss) TextView picassoCacheMissView;
  @InjectView(R.id.debug_picasso_decoded) TextView picassoDecodedView;
  @InjectView(R.id.debug_picasso_decoded_total) TextView picassoDecodedTotalView;
  @InjectView(R.id.debug_picasso_decoded_avg) TextView picassoDecodedAvgView;
  @InjectView(R.id.debug_picasso_transformed) TextView picassoTransformedView;
  @InjectView(R.id.debug_picasso_transformed_total) TextView picassoTransformedTotalView;
  @InjectView(R.id.debug_picasso_transformed_avg) TextView picassoTransformedAvgView;

  @Override public ViewGroup get(final Activity activity, DFGApplication app) {
    this.app = app;
    this.activity = activity;
    drawerContext = activity;

    activity.setContentView(R.layout.debug_activity_frame);

    // Manually find the debug drawer and inflate the drawer layout inside of it.
    ViewGroup drawer = findById(activity, R.id.debug_drawer);
    LayoutInflater.from(drawerContext).inflate(R.layout.debug_drawer_content, drawer);

    // Inject after inflating the drawer layout so its views are available to inject.
    ButterKnife.inject(this, activity);

    // Set up the contextual actions to watch views coming in and out of the content area.
    Set<ContextualDebugActions.DebugAction<?>> debugActions = Collections.emptySet();
    ContextualDebugActions contextualActions = new ContextualDebugActions(this, debugActions);
    content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

    drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, GravityCompat.END);
    drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerOpened(View drawerView) {
        refreshPicassoStats();
      }
    });

    // If you have not seen the debug drawer before, show it with a message
    if (!seenDebugDrawer.get()) {
      drawerLayout.postDelayed(new Runnable() {
        @Override public void run() {
          drawerLayout.openDrawer(GravityCompat.END);
          Toast.makeText(activity, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
        }
      }, 1000);
      seenDebugDrawer.set(true);
    }

    setupUserInterfaceSection();
    setupBuildSection();
    setupDeviceSection();
    setupPicassoSection();

    return content;
  }

  private void setupUserInterfaceSection() {
    final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(activity);
    uiAnimationSpeedView.setAdapter(speedAdapter);
    final int animationSpeedValue = animationSpeed.get();
    uiAnimationSpeedView.setSelection(
        AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));
    uiAnimationSpeedView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        int selected = speedAdapter.getItem(position);
        if (selected != animationSpeed.get()) {
          Ln.d("Setting animation speed to %sx", selected);
          animationSpeed.set(selected);
          applyAnimationSpeed(selected);
        } else {
          Ln.d("Ignoring re-selection of animation speed %sx", selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });
    // Ensure the animation speed value is always applied across app restarts.
    content.post(new Runnable() {
      @Override public void run() {
        applyAnimationSpeed(animationSpeedValue);
      }
    });

    boolean gridEnabled = pixelGridEnabled.get();
    madgeFrameLayout.setOverlayEnabled(gridEnabled);
    uiPixelGridView.setChecked(gridEnabled);
    uiPixelRatioView.setEnabled(gridEnabled);
    uiPixelGridView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Ln.d("Setting pixel grid overlay enabled to " + isChecked);
        pixelGridEnabled.set(isChecked);
        madgeFrameLayout.setOverlayEnabled(isChecked);
        uiPixelRatioView.setEnabled(isChecked);
      }
    });

    boolean ratioEnabled = pixelRatioEnabled.get();
    madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
    uiPixelRatioView.setChecked(ratioEnabled);
    uiPixelRatioView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Ln.d("Setting pixel scale overlay enabled to " + isChecked);
        pixelRatioEnabled.set(isChecked);
        madgeFrameLayout.setOverlayRatioEnabled(isChecked);
      }
    });

    boolean scalpel = scalpelEnabled.get();
    scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
    uiScalpelView.setChecked(scalpel);
    uiScalpelWireframeView.setEnabled(scalpel);
    uiScalpelView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Ln.d("Setting scalpel interaction enabled to " + isChecked);
        scalpelEnabled.set(isChecked);
        scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
        uiScalpelWireframeView.setEnabled(isChecked);
      }
    });

    boolean wireframe = scalpelWireframeEnabled.get();
    scalpelFrameLayout.setDrawViews(!wireframe);
    uiScalpelWireframeView.setChecked(wireframe);
    uiScalpelWireframeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Ln.d("Setting scalpel wireframe enabled to " + isChecked);
        scalpelWireframeEnabled.set(isChecked);
        scalpelFrameLayout.setDrawViews(!isChecked);
      }
    });
  }

  private void setupBuildSection() {
    buildNameView.setText(BuildConfig.VERSION_NAME);
    buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
    buildShaView.setText(BuildConfig.GIT_SHA);

    try {
      // Parse ISO8601-format time into local time.
      DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
      inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date buildTime = inFormat.parse(BuildConfig.BUILD_TIME);
      buildDateView.setText(DATE_DISPLAY_FORMAT.format(buildTime));
    } catch (ParseException e) {
      throw new RuntimeException("Unable to decode build time: " + BuildConfig.BUILD_TIME, e);
    }
  }

  private void setupDeviceSection() {
    DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
    String densityBucket = getDensityString(displayMetrics);
    deviceMakeView.setText(Utils.truncateAt(Build.MANUFACTURER, 20));
    deviceModelView.setText(Utils.truncateAt(Build.MODEL, 20));
    deviceProductView.setText(Utils.truncateAt(Build.PRODUCT, 20));
    deviceResolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
    deviceDensityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
    deviceReleaseView.setText(Build.VERSION.RELEASE);
    deviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
  }

  private void setupPicassoSection() {
    boolean picassoDebuggingValue = picassoDebugging.get();
    picasso.setDebugging(picassoDebuggingValue);
    picassoIndicatorView.setChecked(picassoDebuggingValue);
    picassoIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        Ln.d("Setting Picasso debugging to " + isChecked);
        picasso.setDebugging(isChecked);
        picassoDebugging.set(isChecked);
      }
    });

    refreshPicassoStats();
  }

  private void refreshPicassoStats() {
    StatsSnapshot snapshot = picasso.getSnapshot();
    String size = getSizeString(snapshot.size);
    String total = getSizeString(snapshot.maxSize);
    int percentage = (int) ((1f * snapshot.size / snapshot.maxSize) * 100);
    picassoCacheSizeView.setText(size + " / " + total + " (" + percentage + "%)");
    picassoCacheHitView.setText(String.valueOf(snapshot.cacheHits));
    picassoCacheMissView.setText(String.valueOf(snapshot.cacheMisses));
    picassoDecodedView.setText(String.valueOf(snapshot.originalBitmapCount));
    picassoDecodedTotalView.setText(getSizeString(snapshot.totalOriginalBitmapSize));
    picassoDecodedAvgView.setText(getSizeString(snapshot.averageOriginalBitmapSize));
    picassoTransformedView.setText(String.valueOf(snapshot.transformedBitmapCount));
    picassoTransformedTotalView.setText(getSizeString(snapshot.totalTransformedBitmapSize));
    picassoTransformedAvgView.setText(getSizeString(snapshot.averageTransformedBitmapSize));
  }

  private void applyAnimationSpeed(int multiplier) {
    try {
      Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
      method.invoke(null, (float) multiplier);
    } catch (Exception e) {
      throw new RuntimeException("Unable to apply animation speed.", e);
    }
  }

  private static String getDensityString(DisplayMetrics displayMetrics) {
    switch (displayMetrics.densityDpi) {
      case DisplayMetrics.DENSITY_LOW:
        return "ldpi";
      case DisplayMetrics.DENSITY_MEDIUM:
        return "mdpi";
      case DisplayMetrics.DENSITY_HIGH:
        return "hdpi";
      case DisplayMetrics.DENSITY_XHIGH:
        return "xhdpi";
      case DisplayMetrics.DENSITY_XXHIGH:
        return "xxhdpi";
      case DisplayMetrics.DENSITY_XXXHIGH:
        return "xxxhdpi";
      case DisplayMetrics.DENSITY_TV:
        return "tvdpi";
      default:
        return "unknown";
    }
  }

  private static String getSizeString(long bytes) {
    String[] units = new String[] {
        "B", "KB", "MB", "GB"
    };
    int unit = 0;
    while (bytes >= 1024) {
      bytes /= 1024;
      unit += 1;
    }
    return bytes + units[unit];
  }
}