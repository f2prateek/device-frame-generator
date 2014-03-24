/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.f2prateek.dfg.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.f2prateek.dfg.DFGApplication;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.ui.MainActivity;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public abstract class AbstractGenerateFrameService extends IntentService
    implements DeviceFrameGenerator.Callback {

  public static final int DFG_NOTIFICATION_ID = 789;
  public static final String KEY_EXTRA_DEVICE = "KEY_EXTRA_DEVICE";

  @Inject NotificationManager notificationManager;
  @Inject Bus bus;

  @InjectExtra(KEY_EXTRA_DEVICE) Device device;
  NotificationCompat.Builder notificationBuilder;

  public AbstractGenerateFrameService(String name) {
    super(name);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ((DFGApplication) getApplication()).inject(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Dart.inject(this, intent.getExtras());
  }

  /**
   * Notify the user of a error.
   *
   * @param failedText Text for notification.
   * @param failedTitle Title for notification.
   */
  @Override
  public void failedImage(String failedTitle, String failedSmallText, String failedText) {
    Notification notification = new NotificationCompat.Builder(this).setTicker(failedTitle)
        .setContentTitle(failedTitle)
        .setContentText(failedSmallText)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(failedSmallText)
            .setBigContentTitle(failedTitle)
            .setSummaryText(failedText))
        .setSmallIcon(R.drawable.ic_action_error)
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
        .build();
    notificationManager.notify(DFG_NOTIFICATION_ID, notification);
  }
}