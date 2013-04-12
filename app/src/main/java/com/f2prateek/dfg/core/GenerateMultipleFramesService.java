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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.ui.MainActivity;
import com.f2prateek.dfg.util.StorageUtils;

import java.util.ArrayList;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class GenerateMultipleFramesService extends AbstractGenerateFrameService {

    private static final String LOGTAG = makeLogTag(GenerateMultipleFramesService.class);
    ArrayList<Uri> mImageUris;
    int imagesProcessed;

    public GenerateMultipleFramesService() {
        super(LOGTAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        // Get all the intent data.
        mImageUris = intent.getParcelableArrayListExtra(AppConstants.KEY_EXTRA_SCREENSHOTS);

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean withShadow = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true);
        boolean withGlare = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true);

        imagesProcessed = 0;
        notifyStarting();
        DeviceFrameGenerator deviceFrameGenerator = new DeviceFrameGenerator(this, this, mDevice, withShadow, withGlare);
        for (Uri uri : mImageUris) {
            String screenshotPath = StorageUtils.getPath(this, uri);
            deviceFrameGenerator.generateFrame(screenshotPath);
        }
        notifyFinished();
    }

    public void notifyStarting() {
        Resources r = getResources();
        Intent nullIntent = new Intent(this, MainActivity.class);
        nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.screenshot_saving_ticker))
                .setContentTitle(r.getString(R.string.screenshot_saving_title))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
                .setProgress(0, 0, true)
                .setWhen(System.currentTimeMillis());

        Notification n = mNotificationBuilder.build();
        n.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(DFG_NOTIFICATION_ID, n);
    }

    @Override
    public void startingImage(Bitmap screenshot) {
        // Don't really do anything
    }

    @Override
    public void doneImage(Uri imageUri) {
        imagesProcessed++;
        mNotificationBuilder
                .setContentText(getResources().getString(R.string.processing_image, imagesProcessed, mImageUris.size()))
                .setProgress(mImageUris.size(), imagesProcessed, false);
        mNotificationManager.notify(DFG_NOTIFICATION_ID, mNotificationBuilder.build());
    }

    public void notifyFinished() {
        BUS.post(new Events.MultipleImagesProcessed(mDevice, imagesProcessed));
        Resources resources = getResources();
        mNotificationBuilder
                .setContentTitle(resources.getString(R.string.screenshot_saved_title))
                .setContentText(resources.getString(R.string.single_screenshot_saved, mDevice.getName()))
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(resources.getString(R.string.multiple_screenshots_saved, imagesProcessed, mDevice.getName())))
                .setContentText("")
                .setProgress(0, 0, false);
        mNotificationManager.notify(DFG_NOTIFICATION_ID, mNotificationBuilder.build());
    }

}