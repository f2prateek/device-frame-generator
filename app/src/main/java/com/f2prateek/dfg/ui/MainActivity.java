/**
 * Copyright 2013 prateek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    Bus BUS;
    @InjectView(R.id.pager)
    ViewPager pager;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager.setAdapter(new DeviceFragmentPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(sharedPreferences.getInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BUS.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
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
                fragment.show(getSupportFragmentManager(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        BUS.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
        Device device = DeviceProvider.getDevices().get(event.newDevice);
        Crouton.cancelAllCroutons();
        Crouton.makeText(this, getString(R.string.saved_as_default_message, device.getName()), Style.CONFIRM).show();
        supportInvalidateOptionsMenu();
    }

    @Subscribe
    public void onSingleImageProcessed(Events.SingleImageProcessed event) {
        Crouton.cancelAllCroutons();
        Crouton.makeText(this, getString(R.string.single_screenshot_saved, event.device.getName()), Style.CONFIRM).show();
    }

    @Subscribe
    public void onMultipleImagesProcessed(Events.MultipleImagesProcessed event) {
        Crouton.cancelAllCroutons();
        Crouton.makeText(this, getString(R.string.multiple_screenshots_saved, event.count, event.device.getName()), Style.CONFIRM).show();
    }

    public void updateGlareSetting(boolean newSetting) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AppConstants.KEY_PREF_OPTION_GLARE, newSetting);
        editor.commit();
        Crouton.cancelAllCroutons();
        if (newSetting) {
            Crouton.makeText(this, R.string.glare_enabled, Style.CONFIRM).show();
        } else {
            Crouton.makeText(this, R.string.glare_disabled, Style.ALERT).show();
        }
        invalidateOptionsMenu();
    }

    public void updateShadowSetting(boolean newSetting) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, newSetting);
        editor.commit();
        Crouton.cancelAllCroutons();
        if (newSetting) {
            Crouton.makeText(this, R.string.shadow_enabled, Style.CONFIRM).show();
        } else {
            Crouton.makeText(this, R.string.shadow_disabled, Style.ALERT).show();
        }
        invalidateOptionsMenu();
    }
}
