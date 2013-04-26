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

package com.f2prateek.dfg.test;

import android.support.v4.view.ViewPager;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.ui.MainActivity;
import com.squareup.spoon.Spoon;

import java.util.Random;

import static org.fest.assertions.api.ANDROID.assertThat;

/**
 * Tests for displaying a specific {@link MainActivity} item
 */
public class MainActivityTest extends ActivityTest<MainActivity> {

    private ViewPager pager;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pager = (ViewPager) activity.findViewById(R.id.pager);
    }

    public void testAllDevicesShown() throws Exception {
        final String tag_prefix = "pager_item_";
        assertThat(activity).isNotNull();
        assertThat(pager).isNotNull();
        assertThat(pager.getAdapter()).isNotNull().hasCount(DeviceProvider.getDevices().size());

        for (int i = 0; i < pager.getAdapter().getCount(); i++) {
            final int count = i;
            instrumentation.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    pager.setCurrentItem(count);
                }
            });
            instrumentation.waitForIdleSync();
            Spoon.screenshot(activity, tag_prefix + count);
        }
    }

    public void testDeviceIsSaved() throws Exception {
        Spoon.screenshot(activity, "original_default_device");
        final int device = new Random().nextInt(pager.getAdapter().getCount());
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                pager.setCurrentItem(device);
            }
        });
        solo.clickOnActionBarItem(R.id.menu_default_device);
        activity.finish();
        activity = getActivity();
        assertThat(pager).hasCurrentItem(device);
        Spoon.screenshot(activity, "updated_default_device");
    }

    public void testAboutDialogIsShown() throws Exception {
        solo.clickOnMenuItem("About");
        Spoon.screenshot(activity, "about_dialog");
    }

}
