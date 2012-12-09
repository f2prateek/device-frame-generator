
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class SamsungGalaxyTab2 extends Device {

    private static final String id = AppConstants.SAMSUNG_GALAXY_TAB_2;

    private static final String title = "Samsung Galaxy Tab 2";

    private static final String url = "http://www.samsung.com/global/microsite/galaxytab2/7.0/index.html?type=find";

    private static final float physicalSize = 7f;

    private static final int density = 160;

    private static final int[] landOffset = {
            230, 203
    };

    private static final int[] portOffset = {
            274, 222
    };

    private static final int[] portSize = {
            600, 1024
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
        return R.drawable.samsung_galaxy_tab_2_7inch_thumb;
    }

}
