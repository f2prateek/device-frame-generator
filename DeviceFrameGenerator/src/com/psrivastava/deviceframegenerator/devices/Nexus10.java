
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class Nexus10 extends Device {

    private static final String id = AppConstants.NEXUS_10;

    private static final String title = "Nexus 10";

    private static final String url = "http://www.google.com/nexus/10/";

    private static final float physicalSize = 10f;

    private static final int density = 320;

    private static final int[] landOffset = {
            227, 217
    };

    private static final int[] portOffset = {
            217, 223
    };

    private static final int[] portSize = {
            1600, 2560
    };

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public float getPhysicalSize() {
        return physicalSize;
    }

    @Override
    public int getDensity() {
        return density;
    }

    @Override
    public int[] getLandOffset() {
        return landOffset;
    }

    @Override
    public int[] getPortOffset() {
        return portOffset;
    }

    @Override
    public int[] getPortSize() {
        return portSize;
    }

    @Override
    public int getThumbnail() {
        return R.drawable.nexus_10_thumb;
    }

}
