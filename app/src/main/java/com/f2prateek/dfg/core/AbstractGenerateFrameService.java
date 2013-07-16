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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.DFGApplication;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public abstract class AbstractGenerateFrameService extends IntentService
    implements DeviceFrameGenerator.Callback {

  public static final int DFG_NOTIFICATION_ID = 789;
  protected NotificationCompat.Builder notificationBuilder;
  protected Device device;
  @Inject NotificationManager notificationManager;
  @Inject Bus bus;

  public AbstractGenerateFrameService(String name) {
    super(name);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    DFGApplication.getInstance().inject(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    device = intent.getParcelableExtra(AppConstants.KEY_EXTRA_DEVICE);
  }

  /**
   * Notify the user of a error.
   *
   * @param failed_text Text for notification.
   * @param failed_title Title for notification.
   */
  @Override
  public void failedImage(String failed_title, String failed_small_text, String failed_text) {
    Notification notification = new NotificationCompat.Builder(this).setTicker(failed_title)
        .setContentTitle(failed_title)
        .setContentText(failed_small_text)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(failed_small_text)
            .setBigContentTitle(failed_title)
            .setSummaryText(failed_text))
        .setSmallIcon(R.drawable.ic_action_error)
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        .build();
    notificationManager.notify(DFG_NOTIFICATION_ID, notification);
  }
}