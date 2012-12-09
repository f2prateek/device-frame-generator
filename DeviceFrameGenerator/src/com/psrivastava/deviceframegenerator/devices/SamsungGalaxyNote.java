
package com.psrivastava.deviceframegenerator.devices;

import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.R;

public class SamsungGalaxyNote extends Device {

    private static final String id = AppConstants.SAMSUNG_GALAXY_NOTE;

    private static final String title = "Samsung Galaxy Note";

    private static final String url = "http://www.samsung.com/global/microsite/galaxynote/note/index.html?type=find";

    private static final float physicalSize = 5.3f;

    private static final int density = 320;

    private static final int[] landOffset = {
            353, 209
    };

    private static final int[] portOffset = {
            289, 312
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
        return R.drawable.samsung_galaxy_note_thumb;
    }

}
