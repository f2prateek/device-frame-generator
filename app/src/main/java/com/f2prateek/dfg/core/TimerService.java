package com.f2prateek.dfg.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.f2prateek.dfg.R;
import com.f2prateek.dfg.ui.BootstrapTimerActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import roboguice.service.RoboService;
import roboguice.util.Ln;

import static com.f2prateek.dfg.core.Constants.Notification.TIMER_NOTIFICATION_ID;

public class TimerService extends RoboService {

    @Inject protected Bus BUS;
    @Inject private NotificationManager notificationManager;

    private boolean timerRunning = false;
    private boolean timerStarted;
    private long base;
    private long currentRunningTimeInMillis;
    private long pausedBaseTime;
    private boolean isPaused;

    public static final int TICK_WHAT = 2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the bus so we can send notifications.
        BUS.register(this);

    }

    @Override
    public void onDestroy() {

        // Unregister bus, since its not longer needed as the service is shutting down
        BUS.unregister(this);

        notificationManager.cancel(TIMER_NOTIFICATION_ID);

        Ln.d("Service has been destroyed");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(timerStarted == false) {

            timerStarted = true;

            startTimer();

            // Run as foreground service: http://stackoverflow.com/a/3856940/5210
            // Another example: https://github.com/commonsguy/cw-android/blob/master/Notifications/FakePlayer/src/com/commonsware/android/fakeplayerfg/PlayerService.java
            startForeground(TIMER_NOTIFICATION_ID, getNotification(getString(R.string.timer_running)));
        }

        return START_NOT_STICKY;
    }

    @Produce
    public TimerTickEvent produceTickEvent() {
        return new TimerTickEvent(currentRunningTimeInMillis);
    }

    @Produce
    public TimerPausedEvent produceTimerIsPausedEvent() {
        return new TimerPausedEvent(isPaused);
    }

    @Subscribe
    public void onStopEvent(StopTimerEvent stopEvent) {

        timerHandler.removeMessages(TICK_WHAT);
        stopSelf();
    }

    @Subscribe
    public void onPauseEvent(PauseTimerEvent pauseEvent) {
        pauseTimer();
    }

    /**
     * Pauses the active running timer and updates the notification in the status bar.
     */
    private void pauseTimer() {

        updateNotification(getString(R.string.timer_is_paused));

        timerHandler.removeMessages(TICK_WHAT);
        pausedBaseTime = SystemClock.elapsedRealtime() - base;
        timerRunning = false;
        isPaused = true;

        produceTimerIsPausedEvent();
    }

    @Subscribe
    public void onResumeTimerEvent(ResumeTimerEvent resumeTimerEvent) {
        startTimer();
    }

    private void startTimer() {
        startChronoTimer();
        notifyTimerRunning();
    }

    private void startChronoTimer() {
        base = SystemClock.elapsedRealtime();

        // If coming from a paused state, then find our true base.
        if(pausedBaseTime > 0)
            base = base - pausedBaseTime;

        isPaused = false;

        updateRunning();
    }

    /**
     * Starts the generic timer.
     */
    private void updateRunning() {
        if (timerStarted != timerRunning) {
            if (timerStarted) {
                dispatchTimerUpdate(SystemClock.elapsedRealtime());
                timerHandler.sendMessageDelayed(Message.obtain(timerHandler, TICK_WHAT), 1000);
            } else {
                timerHandler.removeMessages(TICK_WHAT);
            }
            timerRunning = timerStarted;
        }
    }

    private Handler timerHandler = new Handler() {
        public void handleMessage(Message m) {
            if (timerRunning) {
                dispatchTimerUpdate(SystemClock.elapsedRealtime());
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
            }
        }
    };

    private void dispatchTimerUpdate(long now) {

        currentRunningTimeInMillis = now - base;
        Ln.d("Elapsed Seconds: " + currentRunningTimeInMillis / 1000);

        BUS.post(produceTickEvent());

    }



    private void notifyTimerRunning() {
        updateNotification(getString(R.string.timer_running));
        produceTimerIsPausedEvent();
    }


    private void updateNotification(String message) {
        notificationManager.notify(TIMER_NOTIFICATION_ID, getNotification(message));

    }

    /**
     * Creates a notification to show in the notification bar
     * @param message the message to display in the notification bar
     * @return a new {@link Notification}
     */
    private Notification getNotification(String message) {
        final Intent i = new Intent(this, BootstrapTimerActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_stat_ab_notification)
                .setContentText(message)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .getNotification();
    }
}
