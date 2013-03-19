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
import android.test.ServiceTestCase;
import android.util.Log;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import org.fest.assertions.api.ANDROID;
import org.fest.assertions.api.Assertions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class GenerateFrameServiceTest extends ServiceTestCase<GenerateFrameService> {

    private static final int WAIT_TIME = 10;

    private static final String LOGTAG = "GenerateFrameService";

    File mScreenShot;
    File mAppDirectory;
    String mGeneratedFilePath;
    Device mDevice;

    public GenerateFrameServiceTest() {
        super(GenerateFrameService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        deleteFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Device-Frame-Generator")); //Start fresh
        mAppDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Device-Frame-Generator");

        // Pick a random device
        mDevice = getRandomDevice();
        mScreenShot = makeTestScreenShot(mDevice);
    }

    public void testFrameGeneration() throws Exception {
        Log.i(LOGTAG, String.format("Starting test for device %s from screenshot %s. Output is in %s.",
                mDevice.getName(), mScreenShot.getAbsolutePath(), mAppDirectory.getAbsolutePath()));

        Assertions.assertThat(new File(mScreenShot.getAbsolutePath())).isNotNull().isFile();
        Assertions.assertThat(mAppDirectory).isNotNull(); // Don't test for it being a directory yet.

        Intent intent = new Intent(getSystemContext(), GenerateFrameService.class);
        intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, mDevice);
        intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, mScreenShot.getAbsolutePath());
        startService(intent);
        ANDROID.assertThat(getService()).isNotNull();

        Thread.sleep(WAIT_TIME * 1000);

        Assertions.assertThat(mAppDirectory).isDirectory();
        mGeneratedFilePath = getGeneratedImagePath();
        Assertions.assertThat(mGeneratedFilePath).isNotNull();

        File generatedImage = new File(mAppDirectory.getAbsolutePath(), mGeneratedFilePath);
        Assertions.assertThat(generatedImage).isNotNull().isFile();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap b = BitmapFactory.decodeFile(mAppDirectory.getAbsolutePath() + File.separator + mGeneratedFilePath, options);
        ANDROID.assertThat(b).isNotNull();
    }

    @Override
    public void tearDown() throws Exception {
        // Delete our files.
        deleteFile(mScreenShot);
        deleteFile(mAppDirectory);
        super.tearDown();
    }

    /**
     * Delete a file.
     * If it is a folder, delete all files recursively.
     *
     * @param file
     */
    private void deleteFile(File file) {
        Log.d(LOGTAG, "deleting : " + file.getAbsolutePath());
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    deleteFile(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            //if file, then delete it
            file.delete();
        }
    }

    /**
     * Get the generated image path.
     * Looks through mAppDirectory and returns the first image.
     *
     * @return
     */
    private String getGeneratedImagePath() {
        String files[] = mAppDirectory.list();
        if (files.length == 0) {
            return null;
        } else {
            return files[0];
        }
    }

    /**
     * Get a random device.
     */
    private Device getRandomDevice() {
        int random = new Random().nextInt(DeviceProvider.getDevices().size());
        Device device = DeviceProvider.getDevices().get(random);
        return device;
    }

    /**
     * Make a screenshot matching this device's dimension.
     */
    private File makeTestScreenShot(Device device) throws IOException {
        File screenshot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.png");
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp;
        if (new Random().nextBoolean()) {
            bmp = Bitmap.createBitmap(device.getPortSize()[1], device.getPortSize()[0], conf);
        } else {
            bmp = Bitmap.createBitmap(device.getPortSize()[0], device.getPortSize()[1], conf);
        }
        OutputStream os = new FileOutputStream(screenshot.getAbsolutePath());
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        os.flush();
        os.close();
        bmp.recycle();
        return screenshot;
    }

}
