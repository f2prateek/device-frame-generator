package com.f2prateek.dfg.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.PauseTimerEvent;
import com.f2prateek.dfg.core.ResumeTimerEvent;
import com.f2prateek.dfg.core.StopTimerEvent;
import com.f2prateek.dfg.core.TimerPausedEvent;
import com.f2prateek.dfg.core.TimerService;
import com.f2prateek.dfg.core.TimerTickEvent;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.inject.InjectView;

public class BootstrapTimerActivity extends RoboSherlockFragmentActivity implements View.OnClickListener {

    @Inject protected Bus BUS;

    @InjectView(R.id.chronometer) protected TextView chronometer;
    @InjectView(R.id.start) protected Button start;
    @InjectView(R.id.stop) protected Button stop;
    @InjectView(R.id.pause) protected Button pause;
    @InjectView(R.id.resume) protected Button resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bootstrap_timer);

        setTitle(R.string.timer);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        BUS.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        BUS.unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start:
                startTimer();
                break;
            case R.id.stop:
                produceStopEvent();
                break;
            case R.id.pause:
                producePauseEvent();
                break;
            case R.id.resume:
                produceResumeEvent();
                break;
        }
    }

    /**
     * Starts the timer service
     */
    private void startTimer() {
        if(isTimerServiceRunning() == false) {
            final Intent i = new Intent(this, TimerService.class);
            startService(i);

            start.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Posts a {@link StopTimerEvent} message to the {@link Bus}
     */
    private void produceStopEvent() {
        BUS.post(new StopTimerEvent());
    }

    /**
     * Posts a {@link PauseTimerEvent} message to the {@link Bus}
     */
    private void producePauseEvent() {
        BUS.post(new PauseTimerEvent());
    }

    /**
     * Posts a {@link ResumeTimerEvent} message to the {@link Bus}
     */
    private void produceResumeEvent() {
        BUS.post(new ResumeTimerEvent());
    }

    @Subscribe
    public void onTimerPausedEvent(TimerPausedEvent event) {
        if(event.isTimerIsPaused()) {
            resume.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
        } else if(isTimerServiceRunning()) {
            pause.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            resume.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
        }
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     * @param event The event
     */
    @Subscribe
    public void onTickEvent(TimerTickEvent event) {
        setFormattedTime(event.getMillis());
    }



    /**
     * Called by {@link Bus} when a tick event occurs.
     * @param event The event
     */
    @Subscribe
    public void onPauseEvent(PauseTimerEvent event) {
        resume.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     * @param event The event
     */
    @Subscribe
    public void onResumeEvent(ResumeTimerEvent event) {
        resume.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     * @param event The event
     */
    @Subscribe
    public void onStopEvent(StopTimerEvent event) {
        resume.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.GONE);
        setFormattedTime(0); // Since its stopped, zero out the timer.
    }

    /**
     * Checks to see if the timer service is running or not.
     * @return true if the service is running otherwise false.
     */
    private boolean isTimerServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TimerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the formatted time
     * @param millis the elapsed time
     */
    private void setFormattedTime(long millis) {
        final String formattedTime = formatTime(millis);
        chronometer.setText(formattedTime);
    }

    /**
     * Formats the time to look like "HH:MM:SS"
     * @param millis The number of elapsed milliseconds
     * @return A formatted time value
     */
    public static String formatTime(long millis) {

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        // HH:MM:SS
        return String.format("%1$s:%2$s:%3$s" , hoursD , minutesD , secondsD);

    }


}
