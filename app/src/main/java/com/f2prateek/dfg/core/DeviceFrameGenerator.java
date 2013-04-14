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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.util.BitmapUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class DeviceFrameGenerator {

    private static final String LOGTAG = makeLogTag(DeviceFrameGenerator.class);
    private static LruCache<String, Bitmap> mMemoryCache;
    private final Context mContext;
    private final Callback mCallback;
    private final Device mDevice;
    boolean withShadow;
    boolean withGlare;

    public DeviceFrameGenerator(Context context, Callback callback, Device device, boolean withShadow, boolean withGlare) {
        mContext = context;
        mCallback = callback;
        mDevice = device;
    }

    /**
     * Checks if screenshot matches the aspect ratio of the device.
     *
     * @param device     The Device to frame.
     * @param screenshot The screenshot to frame.
     * @return "port" if matched to portrait and "land" if matched to landscape
     * @throws UnmatchedDimensionsException If it could not match any orientation to the device.
     */
    private static String checkDimensions(Device device, Bitmap screenshot) throws UnmatchedDimensionsException {
        float aspect1 = (float) screenshot.getHeight() / (float) screenshot.getWidth();
        float aspect2 = (float) device.getPortSize()[1] / (float) device.getPortSize()[0];

        if (aspect1 == aspect2) {
            return "port";
        } else if (aspect1 == 1 / aspect2) {
            return "land";
        }

        Log.e(LOGTAG, String.format(
                "Screenshot height = %d, width = %d. Device height = %d, width = %d. Aspect1 = %f, Aspect 2 = %f",
                screenshot.getHeight(), screenshot.getWidth(), device.getPortSize()[1], device.getPortSize()[0],
                aspect1, aspect2));
        throw new UnmatchedDimensionsException(device, screenshot.getHeight(), screenshot.getWidth());
    }

    /**
     * Check if the orientation is portrait
     *
     * @param orientation Orientation to check.
     * @return true if orientation is portrait
     */
    private static boolean isPortrait(String orientation) {
        return (orientation.compareTo("port") == 0);
    }

    private static void buildImageCache() {
        if (mMemoryCache != null) {
            return;
        }
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return BitmapUtils.getByteCount(bitmap) / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Generate the frame.
     *
     * @param screenshotPath path to the screenshot file.
     */
    public void generateFrame(String screenshotPath) {
        Log.i(LOGTAG, String.format("Generating for %s %s and %s from file %s.", mDevice.getName(),
                withGlare ? " with glare " : " without glare ",
                withShadow ? " with shadow " : " without shadow ",
                screenshotPath));

        final Bitmap screenshot;
        try {
            screenshot = BitmapUtils.decodeFile(screenshotPath);
        } catch (IOException e) {
            Resources r = mContext.getResources();
            mCallback.failedImage(r.getString(R.string.failed_open_screenshot_title),
                    r.getString(R.string.failed_open_screenshot_text),
                    r.getString(R.string.failed_open_screenshot_text, screenshotPath));
            return;
        }
        generateFrame(screenshot);
    }

    /**
     * Generate the frame.
     *
     * @param screenshot Screenshot to use.
     */
    private void generateFrame(Bitmap screenshot) {
        mCallback.startingImage(screenshot);
        buildImageCache();
        String orientation;
        try {
            orientation = checkDimensions(mDevice, screenshot);
        } catch (UnmatchedDimensionsException e) {
            Log.e(LOGTAG, e.toString());
            Resources r = mContext.getResources();
            String failed_title = r.getString(R.string.failed_match_dimensions_title);
            String failed_text = r.getString(R.string.failed_match_dimensions_text,
                    e.device.getPortSize()[0], e.device.getPortSize()[1],
                    e.screenshotHeight, e.screenshotWidth);
            String failed_small_text = r.getString(R.string.device_chosen, mDevice.getName());
            mCallback.failedImage(failed_title, failed_small_text, failed_text);
            return;
        }

        Bitmap background = getBitmapFromMemCache(mDevice.getId() + "_" + orientation + "_background");
        Bitmap glare = getBitmapFromMemCache(mDevice.getId() + "_" + orientation + "_glare");
        Bitmap shadow = getBitmapFromMemCache(mDevice.getId() + "_" + orientation + "_shadow");
        try {
            if (background == null) {
                background = BitmapUtils.decodeResource(mContext, mDevice.getBackgroundString(orientation));
                addBitmapToMemoryCache(mDevice.getId() + "_" + orientation + "_background", background);
            }
            if (glare == null) {
                glare = BitmapUtils.decodeResource(mContext, mDevice.getGlareString(orientation));
                addBitmapToMemoryCache(mDevice.getId() + "_" + orientation + "_glare", glare);
            }
            if (shadow == null) {
                shadow = BitmapUtils.decodeResource(mContext, mDevice.getShadowString(orientation));
                addBitmapToMemoryCache(mDevice.getId() + "_" + orientation + "_shadow", shadow);
            }
        } catch (IOException e) {
            Log.e(LOGTAG, e.toString());
            Resources r = mContext.getResources();
            mCallback.failedImage(r.getString(R.string.unknown_error_title),
                    r.getString(R.string.unknown_error_text),
                    r.getString(R.string.unknown_error_text));
            return;
        }

        Canvas frame;
        if (withShadow) {
            frame = new Canvas(shadow);
            frame.drawBitmap(background, 0f, 0f, null);
        } else {
            frame = new Canvas(background);
        }

        final int[] offset;
        if (isPortrait(orientation)) {
            screenshot = Bitmap.createScaledBitmap(screenshot, mDevice.getPortSize()[0],
                    mDevice.getPortSize()[1], false);
            offset = mDevice.getPortOffset();
        } else {
            screenshot = Bitmap.createScaledBitmap(screenshot, mDevice.getPortSize()[1],
                    mDevice.getPortSize()[0], false);
            offset = mDevice.getLandOffset();
        }
        frame.drawBitmap(screenshot, offset[0], offset[1], null);

        if (withGlare) {
            frame.drawBitmap(glare, 0f, 0f, null);
        }

        ImageMetadata imageMetadata = prepareMetadata();
        // Save the screenshot to the MediaStore
        ContentValues values = new ContentValues();
        ContentResolver resolver = mContext.getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, imageMetadata.imageFilePath);
        values.put(MediaStore.Images.ImageColumns.TITLE, imageMetadata.imageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageMetadata.imageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream out = resolver.openOutputStream(imageUri);
            if (withShadow) {
                shadow.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else {
                background.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(LOGTAG, e.toString());
            Resources r = mContext.getResources();
            mCallback.failedImage(r.getString(R.string.unknown_error_title),
                    r.getString(R.string.unknown_error_text),
                    r.getString(R.string.unknown_error_text));
            return;
        }

        screenshot.recycle();

        // update file size in the database
        values.clear();
        values.put(MediaStore.Images.ImageColumns.SIZE, new File(imageMetadata.imageFilePath).length());
        resolver.update(imageUri, values, null, null);

        mCallback.doneImage(imageUri);
    }

    /**
     * Prepare the metadata for our image.
     *
     * @return {@link ImageMetadata} that will be used for the image.
     */
    private ImageMetadata prepareMetadata() {
        ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.imageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(imageMetadata.imageTime));
        String imageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        imageMetadata.imageFileName = String.format(AppConstants.DFG_FILE_NAME_TEMPLATE, imageDate);
        imageMetadata.imageFilePath = String.format(AppConstants.DFG_FILE_PATH_TEMPLATE, imageDir,
                AppConstants.DFG_DIR_NAME, imageMetadata.imageFileName);
        return imageMetadata;
    }

    // Views should have these methods to notify the user.
    public interface Callback {
        public void startingImage(Bitmap screenshot);

        public void failedImage(String title, String small_text, String text);

        public void doneImage(Uri imageUri);
    }

    public class ImageMetadata {
        String imageFileName;
        String imageFilePath;
        long imageTime;
    }

}