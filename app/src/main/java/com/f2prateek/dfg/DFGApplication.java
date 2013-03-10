

package com.f2prateek.dfg;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.f2prateek.dfg.model.Device;
import com.google.inject.Injector;
import com.google.inject.Stage;
import roboguice.RoboGuice;

import java.util.ArrayList;

/**
 * Device Frame Generator application
 */
public class DFGApplication extends Application {

    private LruCache<String, Bitmap> mMemoryCache;

    public DFGApplication() {
    }

    public DFGApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    public DFGApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    /**
     * Sets the application injector. Using the {@link RoboGuice#newDefaultRoboModule} as well as a
     * custom binding module {@link DFGModule} to set up your application module
     *
     * @param application
     * @return
     */
    public static Injector setApplicationInjector(Application application) {
        return RoboGuice.setBaseApplicationInjector(application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule
                (application), new DFGModule());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setApplicationInjector(this);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    // TODO : best way to store this?
    private static ArrayList<Device> devices;

    private static ArrayList<Device> generateDevices() {
        ArrayList<Device> devices = new ArrayList<Device>();
        Device nexus_s = new Device.DeviceBuilder().setId("nexus_s").
                setName("Nexus S").setUrl("http://www.google.com/phone/detail/nexus-s").
                setPhysicalSize(4.0f).setDensity(240).
                setLandOffset(new int[]{247, 135}).setPortOffset(new int[]{134, 247}).setPortSize(new int[]{400, 800}).
                setThumbnail(R.drawable.nexus_s_thumb).build();
        devices.add(nexus_s);
        Device galaxy_nexus = new Device.DeviceBuilder().setId("galaxy_nexus").
                setName("Galaxy Nexus").setUrl("http://www.android.com/devices/detail/galaxy-nexus").
                setPhysicalSize(4.65f).setDensity(320).
                setLandOffset(new int[]{371, 199}).setPortOffset(new int[]{216, 353}).setPortSize(new int[]{720, 1280}).
                setThumbnail(R.drawable.galaxy_nexus_thumb).build();
        devices.add(galaxy_nexus);
        return devices;
    }

    public static ArrayList<Device> getDevices() {
        if (devices == null) {
            devices = generateDevices();
        }
        return devices;
    }
}
