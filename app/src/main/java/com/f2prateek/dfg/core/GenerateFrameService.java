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

package com.f2prateek.dfg.core;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;
import static com.f2prateek.dfg.util.StorageUtils.STORAGE_DIRECTORY;

/**
 * A service that generates our frames.
 */
public class GenerateFrameService extends IntentService {

    private static final String LOGTAG = makeLogTag(GenerateFrameService.class);

    public GenerateFrameService() {
        super(LOGTAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this, "starting ", Toast.LENGTH_LONG).show();

        int deviceNum = intent.getIntExtra(AppConstants.KEY_EXTRA_DEVICE, 0);
        Device device = DeviceProvider.getDevices().get(deviceNum);

        boolean withShadow = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_SHADOW, true);
        boolean withGlare = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_GLARE, true);
        String screenshotPath = intent.getStringExtra(AppConstants.KEY_EXTRA_SCREENSHOT);

        try {
            String imagePath = combine(device, screenshotPath, withShadow, withGlare);
            Toast.makeText(this, "done: saved to " + imagePath, Toast.LENGTH_LONG).show();
        } catch (UnmatchedDimensionsException e) {
            // TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            // TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Frames the given screenshot using the parameters
     *
     * @param device         : the device for framing
     * @param screenshotPath : screenshot to be framed
     * @param withShadow     : If should be generated with shadow
     * @param withGlare      : if should be generated with glare
     * @return the path to the saved image
     * @throws UnmatchedDimensionsException couldn't match screenshot to the
     *                                      device
     */
    public String combine(Device device, String screenshotPath,
                          Boolean withShadow, Boolean withGlare) throws UnmatchedDimensionsException, IOException {

        Bitmap screenshot = BitmapUtils.decodeFile(screenshotPath);
        String orientation = checkDimensions(device, screenshot);
        int[] offset = orientation.compareTo("port") == 0 ? device.getPortOffset() : device
                .getLandOffset();

        File folder = new File(STORAGE_DIRECTORY);
        folder.mkdirs();

        Bitmap[] bitmaps = BitmapUtils.decodeDeviceResources(this, device, orientation);
        Bitmap back = bitmaps[0];
        Bitmap shadow = bitmaps[1];
        Bitmap fore = bitmaps[2];

        Canvas comboImage;

        // If shadow is enabled, should be drawn at the bottom of our canvas.
        if (withShadow) {
            comboImage = new Canvas(shadow);
            comboImage.drawBitmap(back, 0f, 0f, null);
        } else {
            comboImage = new Canvas(back);
        }

        comboImage.drawBitmap(back, 0f, 0f, null);
        screenshot.createScaledBitmap(screenshot, device.getPortSize()[0], device.getPortSize()[1],
                false);
        comboImage.drawBitmap(screenshot, offset[0], offset[1], null);

        if (withGlare) {
            comboImage.drawBitmap(fore, 0f, 0f, null);
        }

        // To write the file out to the SDCard:
        OutputStream os = null;

        // Count all files in the directory
        File f = new File(STORAGE_DIRECTORY);
        File[] files = f.listFiles();
        int count = files == null ? 0 : files.length;

        Calendar c = Calendar.getInstance();
        String fileName = device.getId() + "_" + c.get(Calendar.YEAR) + "-"
                + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-"
                + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + count
                + ".png";

        os = new FileOutputStream(STORAGE_DIRECTORY + fileName);
        if (withShadow) {
            shadow.compress(Bitmap.CompressFormat.PNG, 50, os);
        } else {
            back.compress(Bitmap.CompressFormat.PNG, 50, os);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, STORAGE_DIRECTORY + fileName);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        shadow.recycle();
        fore.recycle();
        back.recycle();

        return STORAGE_DIRECTORY + fileName;

    }

    /**
     * Checks if screenshot matches the aspect ratio of the device.
     *
     * @param device
     * @param screenshot
     * @return port if matched to portrait and land if matched to landscape
     * @throws UnmatchedDimensionsException if could not match to the device
     */
    public static String checkDimensions(Device device, Bitmap screenshot)
            throws UnmatchedDimensionsException {

        float aspect1 = (float) screenshot.getHeight() / (float) screenshot.getWidth();
        float aspect2 = (float) device.getPortSize()[1] / (float) device.getPortSize()[0];

        Log.e(LOGTAG,
                "Screenshot height is " + screenshot.getHeight() + " " + device.getPortSize()[1]
                        + " and width is " + screenshot.getWidth() + " " + device.getPortSize()[0]
                        + " aspect1 = " + aspect1 + " aspect2 = " + aspect2);

        if (aspect1 == aspect2) {
            return "port";
        } else if (aspect1 == 1 / aspect2) {
            return "land";
        }

        throw new UnmatchedDimensionsException();

    }

}
