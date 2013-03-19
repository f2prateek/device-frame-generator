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
import android.content.*;
import android.content.res.Resources;
import android.graphics.*;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
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

/**
 * A service that generates our frames.
 */
public class GenerateFrameService extends IntentService {

    private static final String LOGTAG = makeLogTag(GenerateFrameService.class);

    private static final String DFG_DIR_NAME = "/Device-Frame-Generator/";
    private static final String DFG_FILE_NAME_TEMPLATE = "DFG_%s.png";
    private static final String DFG_FILE_PATH_TEMPLATE = "%s/%s/%s";
    private static final int DFG_NOTIFICATION_ID = 789;

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private Notification.BigPictureStyle mNotificationStyle;
    // WORKAROUND: We want the same notification across screenshots that we update so that we don't
    // spam a user's notification drawer.  However, we only show the ticker for the saving state
    // and if the ticker text is the same as the previous notification, then it will not show. So
    // for now, we just add and remove a space from the ticker text to trigger the animation when
    // necessary.
    private static boolean mTickerAddSpace;

    private Device mDevice;

    public GenerateFrameService() {
        super(LOGTAG);
    }

    class ImageMetadata {
        String imageFileName;
        String imageFilePath;
        long imageTime;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get all the intent data.
        mDevice = (Device) intent.getParcelableExtra(AppConstants.KEY_EXTRA_DEVICE);
        String screenshotPath = intent.getStringExtra(AppConstants.KEY_EXTRA_SCREENSHOT);

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean withShadow = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true);
        boolean withGlare = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true);

        Log.d(LOGTAG, String.format("Generating for %s %s and %s from file [%s].", mDevice.getName(),
                withGlare ? " with glare " : " without glare ",
                withShadow ? " with shadow " : " without shadow ",
                screenshotPath));

        ImageMetadata imageMetadata = prepareMetadata();

        try {
            Uri imageUri = generateFrame(screenshotPath, withShadow, withGlare, imageMetadata);
            notifyDone(imageUri);
        } catch (FailedOpenScreenshotException e) {
            GenerateFrameService.notifyError(this, mNotificationManager, R.string.failed_open_screenshot_title, R.string.failed_open_screenshot_text);
            Log.e(LOGTAG, e.toString());
        } catch (UnmatchedDimensionsException e) {
            GenerateFrameService.notifyUnmatchedDimensionsError(this, mNotificationManager, e);
            Log.e(LOGTAG, e.toString());
        } catch (IOException e) {
            GenerateFrameService.notifyError(this, mNotificationManager);
            Log.e(LOGTAG, e.toString());
        }
    }

    /**
     * prepare the metadata for our image
     */
    private ImageMetadata prepareMetadata() {
        // Prepare all the output metadata
        ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.imageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(imageMetadata.imageTime));
        String imageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        imageMetadata.imageFileName = String.format(DFG_FILE_NAME_TEMPLATE, imageDate);
        imageMetadata.imageFilePath = String.format(DFG_FILE_PATH_TEMPLATE, imageDir,
                DFG_DIR_NAME, imageMetadata.imageFileName);
        return imageMetadata;
    }

    /**
     * Use {@link NotificationManager} to provide a notification.
     * TODO : post to bus
     */
    private void notifyStarting(Bitmap screenshot) {
        Resources r = getResources();

        // Create the large notification icon
        int imageWidth = screenshot.getWidth();
        int imageHeight = screenshot.getHeight();
        int iconSize = r.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);

        final int shortSide = imageWidth < imageHeight ? imageWidth : imageHeight;
        Bitmap preview = Bitmap.createBitmap(shortSide, shortSide, screenshot.getConfig());
        Canvas c = new Canvas(preview);
        Paint paint = new Paint();
        ColorMatrix desat = new ColorMatrix();
        desat.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(desat));
        Matrix matrix = new Matrix();
        matrix.postTranslate((shortSide - imageWidth) / 2,
                (shortSide - imageHeight) / 2);
        c.drawBitmap(screenshot, matrix, paint);
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
     * Generate the frame.
     *
     * @param withShadow true if to be drawn with shadow
     * @param withGlare  true if to be drawn with glare
     */
    private Uri generateFrame(String screenshotPath, boolean withShadow, boolean withGlare, ImageMetadata imageMetadata)
            throws FailedOpenScreenshotException, UnmatchedDimensionsException, IOException {

        Bitmap screenshot;
        try {
            screenshot = BitmapUtils.decodeFile(screenshotPath);
        } catch (IOException e) {
            throw new FailedOpenScreenshotException(e);
        }

        notifyStarting(screenshot);

        Canvas frame;
        String orientation = checkDimensions(mDevice, screenshot);

        Bitmap background;
        Bitmap shadow = null;

        background = BitmapUtils.decodeResource(this, mDevice.getBackgroundString(orientation));

        if (withShadow) {
            shadow = BitmapUtils.decodeResource(this, mDevice.getShadowString(orientation));
            frame = new Canvas(shadow);
            frame.drawBitmap(background, 0f, 0f, null);
            background.recycle();
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
            final Bitmap glare = BitmapUtils.decodeResource(this, mDevice.getGlareString(orientation));
            frame.drawBitmap(glare, 0f, 0f, null);
            glare.recycle();
        }

        // Save the screenshot to the MediaStore
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, imageMetadata.imageFilePath);
        values.put(MediaStore.Images.ImageColumns.TITLE, imageMetadata.imageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageMetadata.imageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, imageMetadata.imageTime);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        Intent chooserIntent = Intent.createChooser(sharingIntent, null);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        mNotificationBuilder.addAction(R.drawable.ic_action_share,
                getResources().getString(R.string.share),
                PendingIntent.getActivity(this, 0, chooserIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT));

        OutputStream out = resolver.openOutputStream(imageUri);
        if (withShadow) {
            shadow.compress(Bitmap.CompressFormat.PNG, 100, out);
        } else {
            background.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        out.flush();
        out.close();


        // update file size in the database
        values.clear();
        values.put(MediaStore.Images.ImageColumns.SIZE, new File(imageMetadata.imageFilePath).length());
        resolver.update(imageUri, values, null, null);

        return imageUri;
    }

    /**
     * Notify user when the processing is done.
     */
    private void notifyDone(Uri imageUri) {
        // Show the final notification to indicate screenshot saved
        Resources r = getResources();

        // Create the intent to show the screenshot in gallery
        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setDataAndType(imageUri, "image/png");
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
                "Screenshot height = %d, width = %d. Device height = %d, width = %d. Aspect1 = %f, Aspect 2 = %f",
                screenshot.getHeight(), screenshot.getWidth(), device.getPortSize()[1], device.getPortSize()[0],
                aspect1, aspect2));
        throw new UnmatchedDimensionsException(device, screenshot.getHeight(), screenshot.getWidth());
    }

    /**
     * Notify user with an unknown error.
     *
     * @param context             everything needs a context =(
     * @param notificationManager to display the notification
     */
    static void notifyError(Context context, NotificationManager notificationManager) {
        notifyError(context, notificationManager, R.string.unknown_error_title, R.string.unknown_error_text);
    }

    /**
     * Notify the user of a error.
     *
     * @param context             everything needs a context =(
     * @param notificationManager to display the notification
     * @param failed_text         Text for notification.
     * @param failed_title        Title for notification.
     */
    static void notifyError(Context context, NotificationManager notificationManager, int failed_title, int failed_text) {
        Resources r = context.getResources();
        notifyError(context, notificationManager, r.getString(failed_title), r.getString(failed_text));
    }

    /**
     * Notify the user of a error.
     *
     * @param context             everything needs a context =(
     * @param notificationManager to display the notification
     * @param e                   {@link UnmatchedDimensionsException} that was thrown.
     */
    static void notifyUnmatchedDimensionsError(Context context, NotificationManager notificationManager, UnmatchedDimensionsException e) {
        Resources r = context.getResources();
        String failed_text = r.getString(R.string.failed_match_dimensions_text,
                e.device.getName(), e.device.getPortSize()[0], e.device.getPortSize()[1],
                e.screenshotHeight, e.screenshotWidth);
        notifyError(context, notificationManager, r.getString(R.string.failed_match_dimensions_title), failed_text);
    }

    /**
     * Notify the user of a error.
     *
     * @param context             everything needs a context =(
     * @param notificationManager to display the notification
     * @param failed_text         Text for notification.
     * @param failed_title        Title for notification.
     */
    static void notifyError(Context context, NotificationManager notificationManager, String failed_title, String failed_text) {
        // Clear all existing notification, compose the new notification and show it
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(failed_title)
                .setContentTitle(failed_title)
                .setContentText(failed_text)
                .setSmallIcon(R.drawable.ic_action_error)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .getNotification();
        notificationManager.notify(DFG_NOTIFICATION_ID, notification);
    }

    private static boolean isPortrait(String orientation) {
        return (orientation.compareTo("port") == 0);
    }

}