package com.f2prateek.dfg.core;


/**
 * Event used to pass tick events around through the message bus.
 * This is mainly used in the {@link BootstrapTimer} to show the updates on the timer
 * as the background service runs the timer.
 */
public class TimerTickEvent {
    private final long millis;

    public TimerTickEvent(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

    public long getSeconds() {
        return (millis / 1000);
    }


    @Override
    public String toString() {
        return new StringBuilder("")
                .append("Millis: " + getMillis())
                .append(", ")
                .append("Seconds: " + getSeconds())
                .toString();
    }

}

