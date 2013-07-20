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
import android.net.Uri;
import android.os.Environment;
import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import java.io.File;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

public class GenerateFrameServiceTest
    extends AbstractGenerateFrameServiceTest<GenerateFrameService> {

  private static final int WAIT_TIME = 10;

  public GenerateFrameServiceTest() {
    super(GenerateFrameService.class);
  }

  public void testFrameGeneration() throws Exception {
    deleteFile(
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME));
    File appDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME);

    // Pick a random device
    Device randomDevice = getRandomDevice();
    // Make the test screenshot
    Uri screenshotUri = makeTestScreenShot(randomDevice);

    Intent intent = new Intent(getSystemContext(), GenerateFrameService.class);
    intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, randomDevice);
    intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, screenshotUri);
    startService(intent);
    assertThat(getService()).isNotNull();

    Thread.sleep(WAIT_TIME * 1000);

    assertThat(appDirectory).exists().isDirectory();
    String generatedImagePath = getGeneratedImagePath(appDirectory);
    // The file Path is relative to the app directory, make it absolute
    generatedImagePath = appDirectory + File.separator + generatedImagePath;
    File generatedImage = new File(generatedImagePath);
    assertThat(generatedImage).exists().isFile();

    // Clean up
    ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
        AbstractGenerateFrameService.DFG_NOTIFICATION_ID);
    deleteFile(new File(getPath(screenshotUri)));
    deleteFile(generatedImage);
    deleteFile(appDirectory);
  }
}
