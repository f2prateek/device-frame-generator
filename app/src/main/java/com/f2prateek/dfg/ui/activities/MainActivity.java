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

package com.f2prateek.dfg.ui.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import butterknife.InjectView;
import com.astuetz.PagerSlidingTabStrip;
import com.f2prateek.dfg.DeviceProvider;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.prefs.BlurBackgroundEnabled;
import com.f2prateek.dfg.prefs.ColorBackgroundEnabled;
import com.f2prateek.dfg.prefs.GlareEnabled;
import com.f2prateek.dfg.prefs.ShadowEnabled;
import com.f2prateek.dfg.ui.DeviceFragmentPagerAdapter;
import com.f2prateek.dfg.ui.fragments.AboutFragment;
import com.f2prateek.ln.Ln;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import javax.inject.Inject;
import rx.android.preferences.BooleanPreference;

import static com.f2prateek.dfg.Utils.getColor;

public class MainActivity extends BaseActivity {
  @Inject DeviceProvider deviceProvider;
  @Inject WindowManager windowManager;
  @Inject Analytics analytics;

  @InjectView(R.id.pager) ViewPager pager;
  @InjectView(R.id.tabs) PagerSlidingTabStrip tabStrip;

  @Inject @GlareEnabled BooleanPreference glareEnabled;
  @Inject @ShadowEnabled BooleanPreference shadowEnabled;
  @Inject @ColorBackgroundEnabled BooleanPreference colorBackgroundEnabled;
  @Inject @BlurBackgroundEnabled BooleanPreference blurBackgroundEnabled;

  DeviceFragmentPagerAdapter pagerAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    actionBar.setCustomView(R.layout.action_bar_custom);

    analytics.screen(null, "Main");

    inflateView(R.layout.activity_main);

    // create our manager instance after the content view is set
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Though this could have been set in the theme as well, the launching animation
      // looks a bit jarring (white status bar with white icons on launch)
      // This looks a bit cleaner, turning from black to grey
      setTranslucentStatus(true);

      SystemBarTintManager tintManager = new SystemBarTintManager(this);
      tintManager.setStatusBarTintEnabled(true);
      tintManager.setStatusBarTintResource(R.color.action_bar_color);
    }

    pagerAdapter = new DeviceFragmentPagerAdapter(getFragmentManager(), deviceProvider.asList());
    pager.setAdapter(pagerAdapter);
    pager.setCurrentItem(pagerAdapter.getDeviceIndex(deviceProvider.getDefaultDevice()));
    tabStrip.setTextColor(getColor(this, R.color.title_text_color, Color.WHITE));
    tabStrip.setViewPager(pager);
  }

  @TargetApi(19) private void setTranslucentStatus(boolean on) {
    Window win = getWindow();
    WindowManager.LayoutParams winParams = win.getAttributes();
    final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    if (on) {
      winParams.flags |= bits;
    } else {
      winParams.flags &= ~bits;
    }
    win.setAttributes(winParams);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.activity_main, menu);
    initMenuItem(menu.findItem(R.id.menu_checkbox_glare), glareEnabled);
    initMenuItem(menu.findItem(R.id.menu_checkbox_shadow), shadowEnabled);
    initMenuItem(menu.findItem(R.id.menu_checkbox_blur_background), blurBackgroundEnabled);
    initMenuItem(menu.findItem(R.id.menu_checkbox_color_background), colorBackgroundEnabled);
    return true;
  }

  void initMenuItem(MenuItem menuItem, BooleanPreference preference) {
    menuItem.setChecked(preference.get());
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // views aren't toggled automatically, so we
      // just use the opposite of the state we're in
      case R.id.menu_checkbox_glare:
        updateGlareSetting(!item.isChecked());
        return true;
      case R.id.menu_checkbox_shadow:
        updateShadowSetting(!item.isChecked());
        return true;
      case R.id.menu_checkbox_blur_background:
        updateBlurBackgroundSetting(!item.isChecked());
        return true;
      case R.id.menu_checkbox_color_background:
        updateColorBackgroundSetting(!item.isChecked());
        return true;
      case R.id.menu_match_device:
        analytics.track("Match Device Menu Item Clicked");
        Device device = deviceProvider.find(windowManager);
        if (device == null) {
          analytics.track("Device Not Matched");
          Crouton.makeText(this, R.string.no_matching_device, Style.ALERT).show();
        } else {
          pager.setCurrentItem(pagerAdapter.getDeviceIndex(device));
        }
        return true;
      case R.id.menu_about:
        analytics.track("About Menu Item Clicked");
        final AboutFragment fragment = new AboutFragment();
        fragment.show(getFragmentManager(), "about");
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateGlareSetting(boolean newSettingEnabled) {
    analytics.track("Glare " + (newSettingEnabled ? "Enabled" : "Disabled"));
    analytics.identify(new Traits().putValue("glare_enabled", newSettingEnabled));

    updateBooleanPreference(newSettingEnabled, glareEnabled, getString(R.string.glare_enabled),
        getString(R.string.glare_disabled));
  }

  public void updateShadowSetting(boolean newSettingEnabled) {
    analytics.track("Shadow " + (newSettingEnabled ? "Enabled" : "Disabled"));
    analytics.identify(new Traits().putValue("shadow_enabled", newSettingEnabled));

    updateBooleanPreference(newSettingEnabled, shadowEnabled, getString(R.string.shadow_enabled),
        getString(R.string.shadow_disabled));
  }

  public void updateColorBackgroundSetting(boolean newSettingEnabled) {
    analytics.track("Color Background " + (newSettingEnabled ? "Enabled" : "Disabled"));
    analytics.identify(new Traits().putValue("color_background_enabled", newSettingEnabled));

    updateBooleanPreference(newSettingEnabled, colorBackgroundEnabled,
        getString(R.string.color_background_enabled),
        getString(R.string.color_background_disabled));

    if (newSettingEnabled && blurBackgroundEnabled.get()) {
      // Both blur and color background cannot be enabled together
      updateBlurBackgroundSetting(false);
    }
  }

  public void updateBlurBackgroundSetting(boolean newSettingEnabled) {
    analytics.track("Blur Background " + (newSettingEnabled ? "Enabled" : "Disabled"));
    analytics.identify(new Traits().putValue("blur_background_enabled", newSettingEnabled));

    updateBooleanPreference(newSettingEnabled, blurBackgroundEnabled,
        getString(R.string.blur_background_enabled), getString(R.string.blur_background_disabled));

    if (newSettingEnabled && colorBackgroundEnabled.get()) {
      // Both blur and color background cannot be enabled together
      updateColorBackgroundSetting(false);
    }
  }

  /**
   * Update a boolean preference with the new value.
   * Displays some text to the user dependending on the preference.
   */
  void updateBooleanPreference(boolean newSettingEnabled, BooleanPreference booleanPreference,
      String enabledText, String disabledText) {
    booleanPreference.set(newSettingEnabled);
    if (newSettingEnabled) {
      Crouton.makeText(this, enabledText, Style.CONFIRM).show();
    } else {
      Crouton.makeText(this, disabledText, Style.ALERT).show();
    }
    Ln.d("Setting updated to %s", newSettingEnabled);
    invalidateOptionsMenu();
  }

  @Subscribe public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
    Ln.d("Device updated to %s", event.newDevice.name());
    Properties properties = new Properties();
    event.newDevice.into(properties);
    analytics.track("Updated Default Device", properties);
    Traits traits = new Traits();
    event.newDevice.into(traits);
    analytics.identify(traits);

    Crouton.makeText(this, getString(R.string.saved_as_default_message, event.newDevice.name()),
        Style.CONFIRM).show();
    // This might be from the application class, so update the position as well
    // the application class runs it on the main thread currently, so this more for a
    // future improvement
    pager.setCurrentItem(pagerAdapter.getDeviceIndex(event.newDevice));
    invalidateOptionsMenu();
  }

  @Subscribe public void onSingleImageProcessed(final Events.SingleImageProcessed event) {
    Crouton.makeText(this, getString(R.string.single_screenshot_saved, event.device.name()),
        Style.INFO).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setDataAndType(event.uri, "image/png");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
      }
    }).show();
  }

  @Subscribe public void onMultipleImagesProcessed(final Events.MultipleImagesProcessed event) {
    if (event.uriList.size() == 0) {
      return;
    }
    Crouton.makeText(this,
        getString(R.string.multiple_screenshots_saved, event.uriList.size(), event.device.name()),
        Style.INFO).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setDataAndType(event.uriList.get(0), "image/png");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
      }
    }).show();
  }
}