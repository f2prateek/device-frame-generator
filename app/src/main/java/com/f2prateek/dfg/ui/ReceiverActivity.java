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
import android.util.Log;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
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
                    Log.d("ReceiverActivity", "got single");
                    handleReceivedImage(intent);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    // Got multiple images
                    Log.d("ReceiverActivity", "got multiple");
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
        Intent intent = new Intent(this, GenerateFrameService.class);
        intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, device);
        intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, imageUri);
        startService(intent);
    }

    /**
     * Handle an intent that provides multiple images.
     */
    void handleReceivedMultipleImages(Intent i) {
        ArrayList<Uri> imageUris = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        Device device = getDefaultDeviceFromPreferences();
        Intent intent = new Intent(this, GenerateMultipleFramesService.class);
        intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, device);
        intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOTS, imageUris);
        startService(intent);
    }

    private Device getDefaultDeviceFromPreferences() {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int deviceNum = sPrefs.getInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, 0);
        Device device = DeviceProvider.getDevices().get(deviceNum);
        return device;
    }

}
