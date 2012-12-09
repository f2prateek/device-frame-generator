
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class NexusS extends Device {

    private static final String id = AppConstants.NEXUS_S;

    private static final String title = "Nexus S";

    private static final String url = "http://www.google.com/phone/detail/nexus-s";

    private static final float physicalSize = 4.0f;

    private static final int density = 240;

    private static final int[] landOffset = {
            247, 135
    };

    private static final int[] portOffset = {
            134, 247
    };

    private static final int[] portSize = {
            480, 800
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
        return R.drawable.nexus_s_thumb;
    }

}
