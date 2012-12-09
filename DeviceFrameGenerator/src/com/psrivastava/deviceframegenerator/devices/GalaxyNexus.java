
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class GalaxyNexus extends Device {

    private static final String id = AppConstants.GALAXY_NEXUS;

    private static final String title = "Galaxy Nexus";

    private static final String url = "http://www.android.com/devices/detail/galaxy-nexus";

    private static final float physicalSize = 4.65f;

    private static final int density = 320;

    private static final int[] landOffset = {
            371, 199
    };

    private static final int[] portOffset = {
            216, 353
    };

    private static final int[] portSize = {
            720, 1280
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
        return R.drawable.galaxy_nexus_thumb;
    }

}
