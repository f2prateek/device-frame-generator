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

package com.f2prateek.dfg;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.test.ServiceTestCase;
import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.model.Device;
import java.io.File;
import java.util.ArrayList;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

public class GenerateMultipleFrameServiceTest
    extends ServiceTestCase<GenerateMultipleFramesService> {

  private static final int WAIT_TIME = 30 * 1000; // 30 seconds
  private static final int TEST_SIZE = 4; // no. of images to generate

  public GenerateMultipleFrameServiceTest() {
    super(GenerateMultipleFramesService.class);
  }

  public void testFrameGeneration() throws Exception {
    TestUtils.deleteFile(
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME));
    File appDirectory =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME);

    // Pick a random device
    Device randomDevice = TestUtils.getRandomDevice();
    // Make test screenshots
    ArrayList<Uri> imageUris = new ArrayList<Uri>();
    for (int i = 0; i < TEST_SIZE; i++) {
      imageUris.add(TestUtils.makeTestScreenShot(getSystemContext(), randomDevice));
    }

    Intent intent = new Intent(getSystemContext(), GenerateMultipleFramesService.class);
    intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, randomDevice);
    intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOTS, imageUris);
    startService(intent);
    assertThat(getService()).isNotNull();

    Thread.sleep(WAIT_TIME);

    assertThat(appDirectory).exists().isDirectory();
    assertThat(appDirectory.listFiles()).hasSize(TEST_SIZE);

    // Clean up
    ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
        AbstractGenerateFrameService.DFG_NOTIFICATION_ID);
    for (Uri uri : imageUris) {
      TestUtils.deleteFile(new File(TestUtils.getPath(getSystemContext(), uri)));
    }
    TestUtils.deleteFile(appDirectory);
    MediaScannerConnection.scanFile(getSystemContext(), new String[] { appDirectory.toString() },
        null, null);
  }
}
