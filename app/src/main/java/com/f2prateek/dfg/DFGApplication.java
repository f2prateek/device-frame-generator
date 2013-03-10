package com.f2prateek.dfg;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import com.f2prateek.dfg.model.Device;
import com.google.inject.Injector;
import com.google.inject.Stage;
import roboguice.RoboGuice;

import java.util.ArrayList;

/**
 * Device Frame Generator application
 */
public class DFGApplication extends Application {


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
}
