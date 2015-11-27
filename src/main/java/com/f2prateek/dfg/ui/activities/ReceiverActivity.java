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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.f2prateek.dfg.DeviceProvider;
import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.ln.Ln;
import java.util.ArrayList;
import javax.inject.Inject;

/** A receiver activity, that is registered to receive images. */
public class ReceiverActivity extends BaseActivity {
  @Inject DeviceProvider deviceProvider;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    String action = intent.getAction();
    if (Intent.ACTION_SEND.equals(action)) {
      // Got a single image
      handleReceivedSingleImage(intent);
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
      // Got multiple images
      handleReceivedMultipleImages(intent);
    }

    finish();
  }

  /** Handle an intent that provides a single image. */
  private void handleReceivedSingleImage(Intent i) {
    Uri imageUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
    Ln.d("Generating for single screenshot %s", imageUri);
    Device device = deviceProvider.getDefaultDevice();
    Intent intent = new Intent(this, GenerateFrameService.class);
    intent.putExtra(AbstractGenerateFrameService.KEY_EXTRA_DEVICE, device);
    intent.putExtra(GenerateFrameService.KEY_EXTRA_SCREENSHOT, imageUri);
    startService(intent);
  }

  /** Handle an intent that provides multiple images. */
  void handleReceivedMultipleImages(Intent i) {
    ArrayList<Uri> imageUris = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    Device device = deviceProvider.getDefaultDevice();
    Intent intent = new Intent(this, GenerateMultipleFramesService.class);
    intent.putExtra(AbstractGenerateFrameService.KEY_EXTRA_DEVICE, device);
    intent.putExtra(GenerateMultipleFramesService.KEY_EXTRA_SCREENSHOTS, imageUris);
    Ln.d("Generating for multiple screenshots %s", imageUris);
    startService(intent);
  }
}
