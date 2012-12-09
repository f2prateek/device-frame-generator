
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class SamsungGalaxyS3 extends Device {

    private static final String id = AppConstants.SAMSUNG_GALAXY_S3;

    private static final String title = "Samsung Galaxy S III";

    private static final String url = "http://www.samsung.com/global/galaxys3/";

    private static final float physicalSize = 4.8f;

    private static final int density = 320;

    private static final int[] landOffset = {
            346, 211
    };

    private static final int[] portOffset = {
            302, 307
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
        return R.drawable.samsung_galaxy_s3_thumb;
    }

}
