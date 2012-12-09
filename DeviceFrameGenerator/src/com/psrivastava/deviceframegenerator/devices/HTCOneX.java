
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class HTCOneX extends Device {

    private static final String id = AppConstants.HTC_ONE_X;

    private static final String title = "HTC One X";

    private static final String url = "http://www.htc.com/www/smartphones/htc-one-x/";

    private static final float physicalSize = 4.7f;

    private static final int density = 320;

    private static final int[] landOffset = {
            346, 211
    };

    private static final int[] portOffset = {
            302, 306
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
    public  int getThumbnail() {
        return R.drawable.htc_one_x_thumb;
    }

}
