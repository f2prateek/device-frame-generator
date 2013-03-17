/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.StorageUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import java.util.ArrayList;

public class ReceiverActivity extends RoboSherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    // Got a single image
                    handleReceivedImage(intent);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    // Got multiple images
                    handleReceivedMultipleImages(intent);
                }
            }
        }
    }

    /**
     * Handle an intent that provides a single image.
     */
    private void handleReceivedImage(Intent i) {
        Uri imageUri = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);
        Device device = getDefaultDeviceFromPreferences();
        handleUri(imageUri, device);
    }

    /**
     * Handle an intent that provides multiple images.
     */
    void handleReceivedMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        Device device = getDefaultDeviceFromPreferences();

        for (Uri uri : imageUris) {
            handleUri(uri, device);
        }
    }

    /**
     * Handle an Uri that is the path to a screenshot image.
     */
    private void handleUri(Uri imageUri, Device device) {
        if (imageUri != null) {
            String screenshotPath = StorageUtils.getPath(this, imageUri);
            Intent intent = new Intent(this, GenerateFrameService.class);
            intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, device);
            intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, screenshotPath);
            startService(intent);
        }
    }

    private Device getDefaultDeviceFromPreferences() {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int deviceNum = sPrefs.getInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, 0);
        Device device = DeviceProvider.getDevices().get(deviceNum);
        return device;
    }

}
