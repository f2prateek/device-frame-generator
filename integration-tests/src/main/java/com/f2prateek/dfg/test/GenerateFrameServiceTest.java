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

package com.f2prateek.dfg.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileObserver;
import android.test.ServiceTestCase;
import android.util.Log;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static org.fest.assertions.api.ANDROID.assertThat;

public class GenerateFrameServiceTest extends ServiceTestCase<GenerateFrameService> {

    private static final String screenshotLocation = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/temp.png";
    private static final int SCREEN_WAIT_TIME_SEC = 10;

    Device device;

    public GenerateFrameServiceTest(Class<GenerateFrameService> serviceClass) {
        super(serviceClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        int random = new Random().nextInt(DeviceProvider.getDevices().size());
        device = DeviceProvider.getDevices().get(random);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(device.getPortSize()[1], device.getPortSize()[0], conf);
        FileOutputStream out = new FileOutputStream(screenshotLocation);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    }

    public void testGeneration() throws Exception {
        File screenshotDir = getScreenshotDir();
        NewFileObserver observer = new NewFileObserver(screenshotDir.getAbsolutePath());
        observer.startWatching();

        Intent intent = new Intent(getSystemContext(), GenerateFrameService.class);
        intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, device);
        intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, screenshotLocation);
        startService(intent);
        assertThat(getService()).isNotNull();

        // unlikely, but check if a new screenshot file was already created
        if (observer.getCreatedPath() == null) {
            // wait for screenshot to be created
            synchronized (observer) {
                observer.wait(SCREEN_WAIT_TIME_SEC * 1000);
            }
        }

        assertNotNull(String.format("Could not find screenshot after %d seconds",
                SCREEN_WAIT_TIME_SEC), observer.getCreatedPath());

        File screenshotFile = new File(screenshotDir, observer.getCreatedPath());
        try {
            assertTrue(String.format("Detected new screenshot %s but its not a file",
                    screenshotFile.getName()), screenshotFile.isFile());
            assertTrue(String.format("Detected new screenshot %s but its not an image",
                    screenshotFile.getName()), isValidImage(screenshotFile));
        } finally {
            // delete the file to prevent external storage from filing up
            screenshotFile.delete();
        }

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private File getScreenshotDir() {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Device-Frame-Generator");
    }

    private static class NewFileObserver extends FileObserver {
        private String mAddedPath = null;

        NewFileObserver(String path) {
            super(path, FileObserver.CREATE);
        }

        synchronized String getCreatedPath() {
            return mAddedPath;
        }

        @Override
        public void onEvent(int event, String path) {
            Log.d("DFG_SERVICE_TEST", String.format("Detected new file created %s", path));
            synchronized (this) {
                mAddedPath = path;
                notify();
            }
        }
    }

    private boolean isValidImage(File screenshotFile) {
        Bitmap b = BitmapFactory.decodeFile(screenshotFile.getAbsolutePath());
        return b != null;
    }

}
