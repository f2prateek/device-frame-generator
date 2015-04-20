/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dart.InjectExtra;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.ui.activities.MainActivity;
import java.util.ArrayList;
import javax.inject.Inject;

public class GenerateMultipleFramesService extends AbstractGenerateFrameService {

  public static final String KEY_EXTRA_SCREENSHOTS = "KEY_EXTRA_SCREENSHOTS";

  @Inject Resources resources;

  @InjectExtra(KEY_EXTRA_SCREENSHOTS) ArrayList<Uri> imageUris;
  ArrayList<Uri> processedImageUris;

  public GenerateMultipleFramesService() {
    super("GenerateMultipleFramesService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    super.onHandleIntent(intent);

    notifyStarting();
    processedImageUris = new ArrayList<>();
    for (Uri uri : imageUris) {
      generator.generateFrame(uri);
    }
    notifyFinished();
  }

  public void notifyStarting() {
    Intent nullIntent = new Intent(this, MainActivity.class);
    nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationBuilder = new NotificationCompat.Builder(this).setTicker(
        resources.getString(R.string.screenshot_saving_ticker))
        .setContentTitle(resources.getString(R.string.screenshot_saving_title))
        .setSmallIcon(R.drawable.ic_stat_app_notification)
        .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
        .setProgress(0, 0, true)
        .setWhen(System.currentTimeMillis());

    Notification n = notificationBuilder.build();
    n.flags |= Notification.FLAG_NO_CLEAR;
    notificationManager.notify(DFG_NOTIFICATION_ID, n);
  }

  @Override public void startingImage(Bitmap screenshot) {
    // Don't really do anything
  }

  @Override public void doneImage(Uri imageUri) {
    processedImageUris.add(imageUri);
    notificationBuilder.setContentText(
        getResources().getString(R.string.processing_image, processedImageUris.size(),
            imageUris.size())).setProgress(imageUris.size(), processedImageUris.size(), false);
    notificationManager.notify(DFG_NOTIFICATION_ID, notificationBuilder.build());
  }

  public void notifyFinished() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override public void run() {
        bus.post(new Events.MultipleImagesProcessed(device, processedImageUris));
      }
    });

    if (processedImageUris.size() == 0) {
      return;
    }

    String text =
        resources.getString(R.string.multiple_screenshots_saved, processedImageUris.size(),
            device.name());

    Intent viewImagesIntent = new Intent(Intent.ACTION_VIEW);
    viewImagesIntent.setData(processedImageUris.get(0));
    viewImagesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    notificationBuilder.setContentTitle(resources.getString(R.string.screenshot_saved_title))
        .setContentText(text)
        .setContentIntent(PendingIntent.getActivity(this, 0, viewImagesIntent, 0))
        .setProgress(0, 0, false);
    notificationManager.notify(DFG_NOTIFICATION_ID, notificationBuilder.build());
  }
}