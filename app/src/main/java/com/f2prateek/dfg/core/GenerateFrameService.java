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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.BitmapUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

/**
 * A service that generates our frames.
 */
public class GenerateFrameService extends IntentService {

    private static final String LOGTAG = makeLogTag(GenerateFrameService.class);

    private static final String SCREENSHOTS_DIR_NAME = "/Device-Frame-Generator/";
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s.png";
    private static final String SCREENSHOT_FILE_PATH_TEMPLATE = "%s/%s/%s";
    private static final int SCREENSHOT_NOTIFICATION_ID = 789;

    private int mNotificationId;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private Intent mLaunchIntent;
    private String mImageDir;
    private String mImageFileName;
    private String mImageFilePath;
    private String mImageDate;
    private long mImageTime;

    private static int mNotificationIconSize;
    // WORKAROUND: We want the same notification across screenshots that we update so that we don't
    // spam a user's notification drawer.  However, we only show the ticker for the saving state
    // and if the ticker text is the same as the previous notification, then it will not show. So
    // for now, we just add and remove a space from the ticker text to trigger the animation when
    // necessary.
    private static boolean mTickerAddSpace;

    public GenerateFrameService() {
        super(LOGTAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int deviceNum = intent.getIntExtra(AppConstants.KEY_EXTRA_DEVICE, 0);
        Device device = DeviceProvider.getDevices().get(deviceNum);

        boolean withShadow = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_SHADOW, true);
        boolean withGlare = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_GLARE, true);
        String screenshotPath = intent.getStringExtra(AppConstants.KEY_EXTRA_SCREENSHOT);

        try {
            Uri imageUri = combine(device, screenshotPath, withShadow, withGlare);

            // Show the final notification to indicate screenshot saved
            Resources r = getResources();
            // Create the intent to show the screenshot in gallery
            mLaunchIntent = new Intent(Intent.ACTION_VIEW);
            mLaunchIntent.setDataAndType(imageUri, "image/png");
            mLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mNotificationBuilder
                    .setContentTitle(r.getString(R.string.screenshot_saved_title))
                    .setContentText(r.getString(R.string.screenshot_saved_text))
                    .setContentIntent(PendingIntent.getActivity(this, 0, mLaunchIntent, 0))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
            Notification n = mNotificationBuilder.getNotification();
            n.flags &= ~Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(mNotificationId, n);
        } catch (UnmatchedDimensionsException e) {
            // TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            // TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void setup(Bitmap screenshot) {
        Resources r = getResources();
        // Prepare all the output metadata
        mImageTime = System.currentTimeMillis();
        mImageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(mImageTime));
        mImageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mImageFileName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, mImageDate);
        mImageFilePath = String.format(SCREENSHOT_FILE_PATH_TEMPLATE, mImageDir,
                SCREENSHOTS_DIR_NAME, mImageFileName);

        // Create the large notification icon
        int imageWidth = screenshot.getWidth();
        int imageHeight = screenshot.getHeight();
        mNotificationIconSize = r.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        int iconWidth = mNotificationIconSize;
        int iconHeight = mNotificationIconSize;
        if (imageWidth > imageHeight) {
            iconWidth = (int) (((float) iconHeight / imageHeight) * imageWidth);
        } else {
            iconHeight = (int) (((float) iconWidth / imageWidth) * imageHeight);
        }
        Bitmap rawIcon = Bitmap.createScaledBitmap(screenshot, iconWidth, iconHeight, true);
        Bitmap croppedIcon = Bitmap.createBitmap(rawIcon, (iconWidth - mNotificationIconSize) / 2,
                (iconHeight - mNotificationIconSize) / 2, mNotificationIconSize, mNotificationIconSize);

        // Show the intermediate notification
        mTickerAddSpace = !mTickerAddSpace;
        mNotificationId = SCREENSHOT_NOTIFICATION_ID;
        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(croppedIcon)
                .setTicker(r.getString(R.string.screenshot_saving_ticker)
                        + (mTickerAddSpace ? " " : ""))
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setContentText(r.getString(R.string.screenshot_saving_text))
                .setSmallIcon(R.drawable.ic_action_tick)
                .setWhen(System.currentTimeMillis());
        Notification n = mNotificationBuilder.getNotification();
        n.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(SCREENSHOT_NOTIFICATION_ID, n);
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
    public Uri combine(Device device, String screenshotPath,
                       Boolean withShadow, Boolean withGlare) throws UnmatchedDimensionsException, IOException {

        Bitmap screenshot = BitmapUtils.decodeFile(screenshotPath);
        setup(screenshot);
        String orientation = BitmapUtils.checkDimensions(device, screenshot);
        int[] offset = orientation.compareTo("port") == 0 ? device.getPortOffset() : device
                .getLandOffset();

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

        // Save the screenshot to the MediaStore
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath);
        values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, mImageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, mImageTime);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream out = resolver.openOutputStream(uri);
        if (withShadow) {
            shadow.compress(Bitmap.CompressFormat.PNG, 100, out);
        } else {
            back.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        out.flush();
        out.close();

        // update file size in the database
        values.clear();
        values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
        resolver.update(uri, values, null, null);

        shadow.recycle();
        fore.recycle();
        back.recycle();

        return uri;
    }



}
