
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class Xoom extends Device {

    private static final String id = AppConstants.XOOM;

    private static final String title = "Motorola XOOM";

    private static final String url = "http://www.google.com/phone/detail/motorola-xoom";

    private static final float physicalSize = 10f;

    private static final int density = 160;

    private static final int[] landOffset = {
            218, 191
    };

    private static final int[] portOffset = {
            199, 200
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

    @Override
    public int getThumbnail() {
        return R.drawable.xoom_thumb;
    }

}
