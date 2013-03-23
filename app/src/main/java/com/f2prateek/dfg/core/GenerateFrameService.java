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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.*;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.ui.MainActivity;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

/**
 * A service that generates our frames.
 */
public class GenerateFrameService extends IntentService implements DeviceFrameGenerator.Callback {

    private static final String LOGTAG = makeLogTag(GenerateFrameService.class);

    private static final int DFG_NOTIFICATION_ID = 789;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
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
        // Get all the intent data.
        Device device = (Device) intent.getParcelableExtra(AppConstants.KEY_EXTRA_DEVICE);
        String screenshotPath = intent.getStringExtra(AppConstants.KEY_EXTRA_SCREENSHOT);

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean withShadow = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true);
        boolean withGlare = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true);

        DeviceFrameGenerator deviceFrameGenerator = new DeviceFrameGenerator(this, this);
        deviceFrameGenerator.generateFrame(device, screenshotPath, withShadow, withGlare);
    }

    @Override
    public void notifyStarting(Bitmap screenshot) {
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

        Intent nullIntent = new Intent(this, MainActivity.class);
        nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.screenshot_saving_ticker)
                        + (mTickerAddSpace ? " " : ""))
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setContentText(r.getString(R.string.screenshot_saving_text))
                .setSmallIcon(R.drawable.ic_action_picture)
                .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
                .setWhen(System.currentTimeMillis());

        NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle()
                .bigPicture(preview);
        mNotificationBuilder.setStyle(notificationStyle);

        Notification n = mNotificationBuilder.build();
        n.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(DFG_NOTIFICATION_ID, n);

        // On the tablet, the large icon makes the notification appear as if it is clickable (and
        // on small devices, the large icon is not shown) so defer showing the large icon until
        // we compose the final post-save notification below.
        mNotificationBuilder.setLargeIcon(croppedIcon);
    }

    @Override
    public void notifyFailedOpenScreenshotError(String screenshotPath) {
        notifyError(R.string.failed_open_screenshot_title, R.string.failed_open_screenshot_text);
    }

    @Override
    public void notifyUnmatchedDimensionsError(Device device, int screenhotHeight, int screenshotWidth) {
        Resources r = getResources();
        String failed_title = r.getString(R.string.failed_match_dimensions_title);
        String failed_text = r.getString(R.string.failed_match_dimensions_text,
                device.getName(), device.getPortSize()[0], device.getPortSize()[1],
                screenhotHeight, screenshotWidth);
        notifyError(failed_title, failed_text);
    }

    @Override
    public void notifyFailed() {
        notifyError(R.string.unknown_error_title, R.string.unknown_error_text);
    }

    @Override
    public void notifyDone(Uri imageUri) {
        Resources r = getResources();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        Intent chooserIntent = Intent.createChooser(sharingIntent, null);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mNotificationBuilder.addAction(R.drawable.ic_action_share, getResources().getString(R.string.share),
                PendingIntent.getActivity(this, 0, chooserIntent, PendingIntent.FLAG_CANCEL_CURRENT));

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

    private void notifyError(int failed_title_resource, int failed_text_resource) {
        Resources r = getResources();
        String failed_title = r.getString(failed_title_resource);
        String failed_text = r.getString(failed_text_resource);
        notifyError(failed_title, failed_text);
    }

    /**
     * Notify the user of a error.
     *
     * @param failed_text  Text for notification.
     * @param failed_title Title for notification.
     */
    private void notifyError(String failed_title, String failed_text) {
        // Clear all existing notification, compose the new notification and show it
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(failed_title)
                .setContentTitle(failed_title)
                .setContentText(failed_text)
                .setSmallIcon(R.drawable.ic_action_error)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .getNotification();
        mNotificationManager.notify(DFG_NOTIFICATION_ID, notification);
    }

}