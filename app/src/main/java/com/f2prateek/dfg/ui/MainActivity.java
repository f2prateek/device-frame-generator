/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.InjectView;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import javax.inject.Inject;

public class MainActivity extends BaseActivity {

  @Inject SharedPreferences sharedPreferences;
  @InjectView(R.id.pager) ViewPager pager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pager.setAdapter(new DeviceFragmentPagerAdapter(getFragmentManager()));
    pager.setCurrentItem(sharedPreferences.getInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, 0));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    MenuItem glare = menu.findItem(R.id.menu_checkbox_glare);
    glare.setChecked(sharedPreferences.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true));
    MenuItem shadow = menu.findItem(R.id.menu_checkbox_shadow);
    shadow.setChecked(sharedPreferences.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true));
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
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

  @Subscribe
  public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
    Device device = DeviceProvider.getDevices().get(event.newDevice);
    Crouton.makeText(this, getString(R.string.saved_as_default_message, device.getName()),
        Style.CONFIRM).show();
    invalidateOptionsMenu();
  }

  @Subscribe
  public void onSingleImageProcessed(final Events.SingleImageProcessed event) {
    Crouton.makeText(this, getString(R.string.single_screenshot_saved, event.device.getName()),
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
    Crouton.makeText(this, getString(R.string.multiple_screenshots_saved, event.uriList.size(),
        event.device.getName()), Style.INFO).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setDataAndType(event.uriList.get(0), "image/png");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
      }
    }).show();
  }

  public void updateGlareSetting(boolean newSettingEnabled) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(AppConstants.KEY_PREF_OPTION_GLARE, newSettingEnabled);
    editor.commit();
    if (newSettingEnabled) {
      Crouton.makeText(this, R.string.glare_enabled, Style.CONFIRM).show();
    } else {
      Crouton.makeText(this, R.string.glare_disabled, Style.ALERT).show();
    }
    invalidateOptionsMenu();
  }

  public void updateShadowSetting(boolean newSettingEnabled) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, newSettingEnabled);
    editor.commit();
    if (newSettingEnabled) {
      Crouton.makeText(this, getString(R.string.shadow_enabled), Style.CONFIRM).show();
    } else {
      Crouton.makeText(this, getString(R.string.shadow_disabled), Style.ALERT).show();
    }
    invalidateOptionsMenu();
  }
}