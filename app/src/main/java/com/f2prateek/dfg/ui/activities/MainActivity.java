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

package com.f2prateek.dfg.ui.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.InjectView;
import com.astuetz.PagerSlidingTabStrip;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.prefs.DefaultDevice;
import com.f2prateek.dfg.prefs.GlareEnabled;
import com.f2prateek.dfg.prefs.ShadowEnabled;
import com.f2prateek.dfg.prefs.model.BooleanPreference;
import com.f2prateek.dfg.prefs.model.StringPreference;
import com.f2prateek.dfg.ui.DeviceFragmentPagerAdapter;
import com.f2prateek.dfg.ui.fragments.AboutFragment;
import com.f2prateek.ln.Ln;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;

public class MainActivity extends BaseActivity {

  @Inject @GlareEnabled BooleanPreference glareEnabled;
  @Inject @ShadowEnabled BooleanPreference shadowEnabled;
  @Inject @DefaultDevice StringPreference defaultDevice;

  @Inject Map<String, Device> devices;
  @InjectView(R.id.pager) ViewPager pager;
  @InjectView(R.id.tabs) PagerSlidingTabStrip tabStrip;

  DeviceFragmentPagerAdapter pagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getActionBar().setCustomView(R.layout.action_bar_custom);

    inflateView(R.layout.activity_main);

    pagerAdapter =
        new DeviceFragmentPagerAdapter(getFragmentManager(), new ArrayList<>(devices.values()));
    pager.setAdapter(pagerAdapter);
    pager.setCurrentItem(pagerAdapter.getDeviceIndex(defaultDevice.get()));
    tabStrip.setTextColor(getResources().getColor(R.color.title_text_color));
    tabStrip.setViewPager(pager);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.activity_main, menu);
    initMenuItem(menu.findItem(R.id.menu_checkbox_glare), glareEnabled);
    initMenuItem(menu.findItem(R.id.menu_checkbox_shadow), shadowEnabled);
    return true;
  }

  void initMenuItem(MenuItem menuItem, BooleanPreference preference) {
    menuItem.setChecked(preference.get());
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // items aren't toggled automatically, so we
      // just use the opposite of the state we're in
      case R.id.menu_checkbox_glare:
        updateGlareSetting(!item.isChecked());
        return true;
      case R.id.menu_checkbox_shadow:
        updateShadowSetting(!item.isChecked());
        return true;
      case R.id.menu_about:
        final AboutFragment fragment = new AboutFragment();
        fragment.show(getFragmentManager(), "about");
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateGlareSetting(boolean newSettingEnabled) {
    updateBooleanPreference(newSettingEnabled, glareEnabled, getString(R.string.glare_enabled),
        getString(R.string.glare_disabled));
  }

  public void updateShadowSetting(boolean newSettingEnabled) {
    updateBooleanPreference(newSettingEnabled, shadowEnabled, getString(R.string.shadow_enabled),
        getString(R.string.shadow_disabled));
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

  @Subscribe
  public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
    Ln.d("Device updated to %s", event.newDevice.name());
    Crouton.makeText(this, getString(R.string.saved_as_default_message, event.newDevice.name()),
        Style.CONFIRM).show();
    // this might be from the application class, so update the position as well
    pager.setCurrentItem(pagerAdapter.getDeviceIndex(event.newDevice.id()));
    invalidateOptionsMenu();
  }

  @Subscribe
  public void onSingleImageProcessed(final Events.SingleImageProcessed event) {
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

  @Subscribe
  public void onMultipleImagesProcessed(final Events.MultipleImagesProcessed event) {
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