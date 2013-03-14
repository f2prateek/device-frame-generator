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
import android.graphics.*;
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

    private static final String DFG_DIR_NAME = "/Device-Frame-Generator/";
    private static final String DFG_FILE_NAME_TEMPLATE = "Screenshot_%s.png";
    private static final String DFG_FILE_PATH_TEMPLATE = "%s/%s/%s";
    private static final int DFG_NOTIFICATION_ID = 789;

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private String mImageFileName;
    private String mImageFilePath;
    private long mImageTime;
    private Notification.BigPictureStyle mNotificationStyle;
    // WORKAROUND: We want the same notification across screenshots that we update so that we don't
    // spam a user's notification drawer.  However, we only show the ticker for the saving state
    // and if the ticker text is the same as the previous notification, then it will not show. So
    // for now, we just add and remove a space from the ticker text to trigger the animation when
    // necessary.
    private static boolean mTickerAddSpace;

    private Device mDevice;
    private Uri mImageUri;
    private Bitmap mScreenshot;
    private Bitmap mShadow;
    private Bitmap mGlare;
    private Bitmap mBackground;
    private String mOrientation;

    public GenerateFrameService() {
        super(LOGTAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Get all the intent data.
        mDevice = DeviceProvider.getDevices().get(intent.getIntExtra(AppConstants.KEY_EXTRA_DEVICE, 0));
        String screenshotPath = intent.getStringExtra(AppConstants.KEY_EXTRA_SCREENSHOT);
        boolean withShadow = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_SHADOW, true);
        boolean withGlare = intent.getBooleanExtra(AppConstants.KEY_EXTRA_OPTION_GLARE, true);

        try {
            retrieveScreenshot(screenshotPath);
        } catch (IOException e) {
            GenerateFrameService.notifyError(this, mNotificationManager, R.string.failed_open_screenshot_text, R.string.failed_open_screenshot_title);
            e.printStackTrace();
        }

        prepareMetadata();
        notifyStarting();

        try {
            retrieveResourceBitmaps(withShadow, withGlare);
        } catch (UnmatchedDimensionsException e) {
            GenerateFrameService.notifyError(this, mNotificationManager, R.string.failed_match_dimensions_text, R.string.failed_match_dimensions_title);
            e.printStackTrace();
        } catch (IOException e) {
            GenerateFrameService.notifyError(this, mNotificationManager);
            e.printStackTrace();
        }

        if (generateFrame(withShadow, withGlare) != 0) {
            // Show a message that we've failed to save this
            GenerateFrameService.notifyError(this, mNotificationManager);
        } else {
            notifyDone();
        }

    }

    /**
     * prepare the metadata for our image
     */
    private void prepareMetadata() {
        // Prepare all the output metadata
        mImageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(mImageTime));
        String imageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        mImageFileName = String.format(DFG_FILE_NAME_TEMPLATE, imageDate);
        mImageFilePath = String.format(DFG_FILE_PATH_TEMPLATE, imageDir,
                DFG_DIR_NAME, mImageFileName);
    }

    /**
     * Use {@link NotificationManager} to provide a notification.
     * TODO : post to bus
     */
    private void notifyStarting() {
        Resources r = getResources();

        // Create the large notification icon
        int imageWidth = mScreenshot.getWidth();
        int imageHeight = mScreenshot.getHeight();
        int iconSize = r.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);

        final int shortSide = imageWidth < imageHeight ? imageWidth : imageHeight;
        Bitmap preview = Bitmap.createBitmap(shortSide, shortSide, mScreenshot.getConfig());
        Canvas c = new Canvas(preview);
        Paint paint = new Paint();
        ColorMatrix desat = new ColorMatrix();
        desat.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(desat));
        Matrix matrix = new Matrix();
        matrix.postTranslate((shortSide - imageWidth) / 2,
                (shortSide - imageHeight) / 2);
        c.drawBitmap(mScreenshot, matrix, paint);
        c.drawColor(0x40FFFFFF);

        Bitmap croppedIcon = Bitmap.createScaledBitmap(preview, iconSize, iconSize, true);

        // Show the intermediate notification
        mTickerAddSpace = !mTickerAddSpace;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new Notification.Builder(this)
                .setTicker(r.getString(R.string.screenshot_saving_ticker)
                        + (mTickerAddSpace ? " " : ""))
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setContentText(r.getString(R.string.screenshot_saving_text))
                .setSmallIcon(R.drawable.ic_action_picture)
                .setWhen(System.currentTimeMillis());

        mNotificationStyle = new Notification.BigPictureStyle()
                .bigPicture(preview);
        mNotificationBuilder.setStyle(mNotificationStyle);

        Notification n = mNotificationBuilder.build();
        n.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(DFG_NOTIFICATION_ID, n);

        // On the tablet, the large icon makes the notification appear as if it is clickable (and
        // on small devices, the large icon is not shown) so defer showing the large icon until
        // we compose the final post-save notification below.
        mNotificationBuilder.setLargeIcon(croppedIcon);
        // But we still don't set it for the expanded view, allowing the smallIcon to show here.
        mNotificationStyle.bigLargeIcon(null);
    }

    /**
     * Generate the frame
     *
     * @param withShadow true if to be drawn with shadow
     * @param withGlare  true if to be drawn with glare
     */
    private int generateFrame(boolean withShadow, boolean withGlare) {
        Canvas canvas;

        if (withShadow) {
            canvas = new Canvas(mShadow);
            canvas.drawBitmap(mBackground, 0f, 0f, null);
        } else {
            canvas = new Canvas(mBackground);
        }

        int[] offset = mOrientation.compareTo("port") == 0 ?
                mDevice.getPortOffset() : mDevice.getLandOffset();
        mScreenshot = Bitmap.createScaledBitmap(mScreenshot, mDevice.getPortSize()[0],
                mDevice.getPortSize()[1], false);
        canvas.drawBitmap(mScreenshot, offset[0], offset[1], null);

        if (withGlare) {
            canvas.drawBitmap(mGlare, 0f, 0f, null);
        }

        try {
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
            mImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/png");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);

            Intent chooserIntent = Intent.createChooser(sharingIntent, null);
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            mNotificationBuilder.addAction(R.drawable.ic_action_share,
                    getResources().getString(R.string.share),
                    PendingIntent.getActivity(this, 0, chooserIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT));

            OutputStream out = resolver.openOutputStream(mImageUri);
            if (withShadow) {
                mShadow.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else {
                mBackground.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();

            // update file size in the database
            values.clear();
            values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
            resolver.update(mImageUri, values, null, null);

            return 0;
        } catch (IOException e) {
            return -1;
        }
    }

    private void notifyDone() {
        // Show the final notification to indicate screenshot saved
        Resources r = getResources();

        // Create the intent to show the screenshot in gallery
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setDataAndType(mImageUri, "image/png");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mNotificationBuilder
                .setContentTitle(r.getString(R.string.screenshot_saved_title))
                .setContentText(r.getString(R.string.screenshot_saved_text))
                .setContentIntent(PendingIntent.getActivity(this, 0, launchIntent, 0))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        Notification n = mNotificationBuilder.build();
        n.flags &= ~Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(DFG_NOTIFICATION_ID, n);
    }

    /**
     * Retrieve the screenshot that user has selected.
     *
     * @param screenshotPath Path to screenshot file
     * @throws IOException                  couldn't make the file mutable
     * @throws UnmatchedDimensionsException couldn't match the orientation.
     */
    private void retrieveScreenshot(String screenshotPath) throws IOException {
        mScreenshot = BitmapUtils.decodeFile(screenshotPath);
    }

    /**
     * Get the resources in their right orientation
     *
     * @param withShadow true if shadow should be drawn
     * @param withGlare  true if glare should be drawn
     * @throws IOException                  couldn't make the file mutable
     * @throws UnmatchedDimensionsException couldn't match the orientation.
     */
    private void retrieveResourceBitmaps(boolean withShadow, boolean withGlare) throws IOException,
            UnmatchedDimensionsException {
        mOrientation = checkDimensions(mDevice, mScreenshot);
        mBackground = BitmapUtils.decodeResource(this, mDevice.getBackString(mOrientation));
        mShadow = withShadow ? BitmapUtils.decodeResource(this, mDevice.getShadowString(mOrientation)) : null;
        mGlare = withGlare ? BitmapUtils.decodeResource(this, mDevice.getGlareString(mOrientation)) : null;
    }

    /**
     * Checks if screenshot matches the aspect ratio of the device.
     *
     * @param device     The Device to frame.
     * @param screenshot The screenshot to frame.
     * @return "port" if matched to portrait and "land" if matched to landscape
     * @throws UnmatchedDimensionsException If it could not match any orientation to the device.
     */
    public static String checkDimensions(Device device, Bitmap screenshot) throws UnmatchedDimensionsException {
        float aspect1 = (float) screenshot.getHeight() / (float) screenshot.getWidth();
        float aspect2 = (float) device.getPortSize()[1] / (float) device.getPortSize()[0];

        if (aspect1 == aspect2) {
            return "port";
        } else if (aspect1 == 1 / aspect2) {
            return "land";
        }

        Log.e(LOGTAG, String.format(
                "Screenshot height = %d, width = %d. Device height = %d, width = %d. Aspect1 = %d, Aspect 2 = %d",
                screenshot.getHeight(), screenshot.getWidth(), device.getPortSize()[1], device.getPortSize()[0],
                aspect1, aspect2));
        throw new UnmatchedDimensionsException();
    }

    static void notifyError(Context context, NotificationManager notificationManager) {
        notifyError(context, notificationManager, R.string.unknown_error_text, R.string.unknown_error_title);
    }

    /**
     * Notify the user of a error.
     * TODO : post to Bus
     */
    static void notifyError(Context context, NotificationManager notificationManager, int failed_text, int failed_title) {
        Resources r = context.getResources();
        // Clear all existing notification, compose the new notification and show it
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(r.getString(failed_title))
                .setContentTitle(r.getString(failed_title))
                .setContentText(r.getString(failed_text))
                .setSmallIcon(R.drawable.ic_action_error)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .getNotification();
        notificationManager.notify(DFG_NOTIFICATION_ID, notification);
    }

}