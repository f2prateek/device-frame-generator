/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.ui.MainActivity;

/** A service that generates our frames. */
public class GenerateFrameService extends AbstractGenerateFrameService {

  public GenerateFrameService() {
    super("GenerateFrameService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    super.onHandleIntent(intent);

    SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean withShadow = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true);
    boolean withGlare = sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true);
    boolean cleanStatusBar =
        sPrefs.getBoolean(AppConstants.KEY_PREF_OPTION_CLEAN_STATUS_BAR, false);

    // Get all the intent data.
    Uri imageUri = intent.getParcelableExtra(AppConstants.KEY_EXTRA_SCREENSHOT);
    DeviceFrameGenerator.generate(this, this, device, withShadow, withGlare, cleanStatusBar,
        imageUri);
  }

  @Override
  public void startingImage(Bitmap screenshot) {
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
    matrix.postTranslate((shortSide - imageWidth) / 2, (shortSide - imageHeight) / 2);
    c.drawBitmap(screenshot, matrix, paint);
    c.drawColor(0x40FFFFFF);

    Bitmap croppedIcon = Bitmap.createScaledBitmap(preview, iconSize, iconSize, true);

    Intent nullIntent = new Intent(this, MainActivity.class);
    nullIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationBuilder = new NotificationCompat.Builder(this).setTicker(
        r.getString(R.string.screenshot_saving_ticker))
        .setContentTitle(r.getString(R.string.screenshot_saving_title))
        .setSmallIcon(R.drawable.app_icon)
        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(preview))
        .setContentIntent(PendingIntent.getActivity(this, 0, nullIntent, 0))
        .setWhen(System.currentTimeMillis())
        .setProgress(0, 0, true)
        .setLargeIcon(croppedIcon);

    Notification n = notificationBuilder.build();
    n.flags |= Notification.FLAG_NO_CLEAR;
    notificationManager.notify(DFG_NOTIFICATION_ID, n);
  }

  @Override
  public void doneImage(final Uri imageUri) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        bus.post(new Events.SingleImageProcessed(device, imageUri));
      }
    });
    Resources resources = getResources();
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("image/png");
    sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
    Intent chooserIntent = Intent.createChooser(sharingIntent, null);
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    notificationBuilder.addAction(R.drawable.ic_action_share,
        getResources().getString(R.string.share),
        PendingIntent.getActivity(this, 0, chooserIntent, PendingIntent.FLAG_CANCEL_CURRENT));

    // Create the intent to show the screenshot in gallery
    Intent launchIntent = new Intent(Intent.ACTION_VIEW);
    launchIntent.setDataAndType(imageUri, "image/png");
    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    notificationBuilder.setContentTitle(resources.getString(R.string.screenshot_saved_title))
        .setContentText(resources.getString(R.string.single_screenshot_saved, device.getName()))
        .setContentIntent(PendingIntent.getActivity(this, 0, launchIntent, 0))
        .setWhen(System.currentTimeMillis())
        .setProgress(0, 0, false)
        .setAutoCancel(true);

    notificationManager.notify(DFG_NOTIFICATION_ID, notificationBuilder.build());
  }
}