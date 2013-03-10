/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.model;

import com.f2prateek.dfg.R;

import java.util.ArrayList;

public class DeviceProvider {

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
        Device nexus_4 = new Device.DeviceBuilder().setId("nexus_4").
                setName("Nexus 4").setUrl("http://www.google.com/nexus/4/").
                setPhysicalSize(4.7f).setDensity(320).
                setLandOffset(new int[]{349, 214}).setPortOffset(new int[]{213, 350}).setPortSize(new int[]{768, 1280}).
                setThumbnail(R.drawable.nexus_4_thumb).build();
        devices.add(nexus_4);
        Device nexus_7 = new Device.DeviceBuilder().setId("nexus_7").
                setName("Nexus 7").setUrl("http://www.android.com/devices/detail/nexus-7").
                setPhysicalSize(7f).setDensity(213).
                setLandOffset(new int[]{315, 270}).setPortOffset(new int[]{264, 311}).setPortSize(new int[]{800, 1280}).
                setThumbnail(R.drawable.nexus_7_thumb).build();
        devices.add(nexus_7);
        Device nexus_10 = new Device.DeviceBuilder().setId("nexus_10").
                setName("Nexus 10").setUrl("http://www.google.com/nexus/10/").
                setPhysicalSize(10f).setDensity(320).
                setLandOffset(new int[]{227, 217}).setPortOffset(new int[]{217, 223}).setPortSize(new int[]{800, 1280}).
                setThumbnail(R.drawable.nexus_10_thumb).build();
        devices.add(nexus_10);
        return devices;
    }

    public static ArrayList<Device> getDevices() {
        if (devices == null) {
            devices = generateDevices();
        }
        return devices;
    }

}
