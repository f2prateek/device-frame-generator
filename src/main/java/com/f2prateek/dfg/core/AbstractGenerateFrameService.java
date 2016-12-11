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
import com.f2prateek.dfg.prefs.BackgroundBlurRadius;
import com.f2prateek.dfg.prefs.BackgroundColorOption;
import com.f2prateek.dfg.prefs.BackgroundColorOption.Option;
import com.f2prateek.dfg.prefs.BackgroundPaddingPercentage;
import com.f2prateek.dfg.prefs.BlurBackgroundEnabled;
import com.f2prateek.dfg.prefs.ColorBackgroundEnabled;
import com.f2prateek.dfg.prefs.CustomBackgroundColor;
import com.f2prateek.dfg.prefs.GlareEnabled;
import com.f2prateek.dfg.prefs.ShadowEnabled;
import com.f2prateek.dfg.ui.activities.MainActivity;
import com.f2prateek.rx.preferences.Preference;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public abstract class AbstractGenerateFrameService extends IntentService
    implements DeviceFrameGenerator.Callback {
  static final int DFG_NOTIFICATION_ID = 1;
  public static final String KEY_EXTRA_DEVICE = "KEY_EXTRA_DEVICE";

  @Inject NotificationManager notificationManager;
  @Inject Bus bus;
  @Inject Analytics analytics;

  @Inject @ShadowEnabled Preference<Boolean> shadowEnabledPreference;
  @Inject @GlareEnabled Preference<Boolean> glareEnabledPreference;
  @Inject @BlurBackgroundEnabled Preference<Boolean> blurBackgroundEnabledPreference;
  @Inject @ColorBackgroundEnabled Preference<Boolean> colorBackgroundEnabledPreference;
  @Inject @BackgroundColorOption Preference<Option> backgroundColorOptionPreference;
  @Inject @CustomBackgroundColor Preference<Integer> customBackgroundColorPreference;
  @Inject @BackgroundPaddingPercentage Preference<Integer> backgroundPaddingPercentagePreference;
  @Inject @BackgroundBlurRadius Preference<Integer> backgroundBlurRadiusPreference;

  @InjectExtra(KEY_EXTRA_DEVICE) Device device;
  NotificationCompat.Builder notificationBuilder;
  DeviceFrameGenerator generator;

  public AbstractGenerateFrameService(String name) {
    super(name);
  }

  @Override public void onCreate() {
    super.onCreate();
    ((DFGApplication) getApplication()).inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Dart.inject(this, intent.getExtras());
    Properties properties = new Properties();
    device.into(properties);
    analytics.track("Generating Frame", properties);

    generator = new DeviceFrameGenerator(this, this, device, shadowEnabledPreference.get(),
        glareEnabledPreference.get(), colorBackgroundEnabledPreference.get(),
        blurBackgroundEnabledPreference.get(), backgroundColorOptionPreference.get(),
        customBackgroundColorPreference.get(), backgroundPaddingPercentagePreference.get(),
        backgroundBlurRadiusPreference.get());
  }

  /**
   * Notify the user of a error.
   *
   * @param title Title of the notification.
   * @param text Text of the notification.
   * @param extra Extra information to show to user.
   */
  @Override public void failedImage(String title, String text, String extra) {
    analytics.track("Frame Generation Error",
        new Properties().putValue("title", title).putValue("text", text).putValue("extra", extra));
    Notification notification = new NotificationCompat.Builder(this) //
        .setTicker(title)
        .setContentTitle(title)
        .setContentText(text)
        .setStyle(new NotificationCompat.BigTextStyle() //
            .setBigContentTitle(title) //
            .bigText(text) //
            .setSummaryText(extra))
        .setSmallIcon(R.drawable.ic_action_error)
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
        .build();
    notificationManager.notify(DFG_NOTIFICATION_ID, notification);
  }
}