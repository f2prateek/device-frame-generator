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
    protected void onPause() {
        super.onPause();
        BUS.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem item = menu.add(Menu.NONE, R.id.menu_settings, Menu.FIRST, R.string.menu_settings);
        item.setActionProvider(new CheckBoxActionProvider(this, BUS));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT |
                MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setIcon(R.drawable.ic_action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_default_device:
                saveDeviceAsDefault();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeviceAsDefault() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, pager.getCurrentItem());
        editor.commit();
        String text = getResources().getString(R.string.saved_as_default_message, DeviceProvider.getDevices().get(pager.getCurrentItem()).getName());
        Crouton.cancelAllCroutons();
        Crouton.makeText(this, text, Style.CONFIRM).show();
    }

    @Subscribe
    public void onGlareSettingUpdated(Events.GlareSettingUpdated event) {
        Crouton.cancelAllCroutons();
        if (event.isEnabled) {
            Crouton.makeText(this, R.string.glare_enabled, Style.CONFIRM).show();
        } else {
            Crouton.makeText(this, R.string.glare_disabled, Style.ALERT).show();
        }
    }

    @Subscribe
    public void onShadowSettingUpdated(Events.ShadowSettingUpdated event) {
        Crouton.cancelAllCroutons();
        if (event.isEnabled) {
            Crouton.makeText(this, R.string.shadow_enabled, Style.CONFIRM).show();
        } else {
            Crouton.makeText(this, R.string.shadow_disabled, Style.ALERT).show();
        }
    }
}
