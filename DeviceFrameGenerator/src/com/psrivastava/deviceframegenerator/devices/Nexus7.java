
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class Nexus7 extends Device {

    private static final String id = AppConstants.NEXUS_7;

    private static final String title = "Nexus 7";

    private static final String url = "http://www.android.com/devices/detail/nexus-7";

    private static final float physicalSize = 7f;

    private static final int density = 213;

    private static final int[] landOffset = {
            315, 270
    };

    private static final int[] portOffset = {
            264, 311
    };

    private static final int[] portSize = {
            800, 1280
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

    public int getThumbnail() {
        return R.drawable.nexus_7_thumb;
    }

}
