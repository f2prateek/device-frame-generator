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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.InjectView;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.squareup.otto.Subscribe;
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
    }
    return super.onOptionsItemSelected(item);
  }

  @Subscribe
  public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
    Device device = DeviceProvider.getDevices().get(event.newDevice);
    Toast.makeText(this, getString(R.string.saved_as_default_message, device.getName()),
        Toast.LENGTH_LONG).show();
    invalidateOptionsMenu();
  }

  @Subscribe
  public void onSingleImageProcessed(Events.SingleImageProcessed event) {
    Toast.makeText(this, getString(R.string.single_screenshot_saved, event.device.getName()),
        Toast.LENGTH_LONG).show();
  }

  @Subscribe
  public void onMultipleImagesProcessed(Events.MultipleImagesProcessed event) {
    Toast.makeText(this,
        getString(R.string.multiple_screenshots_saved, event.count, event.device.getName()),
        Toast.LENGTH_LONG).show();
  }

  public void updateGlareSetting(boolean newSetting) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(AppConstants.KEY_PREF_OPTION_GLARE, newSetting);
    editor.commit();
    if (newSetting) {
      Toast.makeText(this, R.string.glare_enabled, Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, R.string.glare_disabled, Toast.LENGTH_LONG).show();
    }
    invalidateOptionsMenu();
  }

  public void updateShadowSetting(boolean newSetting) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, newSetting);
    editor.commit();
    if (newSetting) {
      Toast.makeText(this, getString(R.string.shadow_enabled), Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, getString(R.string.shadow_disabled), Toast.LENGTH_LONG).show();
    }
    invalidateOptionsMenu();
  }
}
