
package com.psrivastava.deviceframegenerator;

import java.util.Arrays;

public abstract class Device {

    @Override
    public String toString() {
        return "Device [title=" + getTitle() + ", portSize=" + Arrays.toString(getPortSize()) + "]";
    }

    public abstract String getId();

    public abstract String getTitle();

    public abstract String getUrl();

    public abstract float getPhysicalSize();

    public abstract int getDensity();

    public abstract int[] getLandOffset();

    public abstract int[] getPortOffset();

    public abstract int[] getPortSize();

    public abstract int getThumbnail();

}
