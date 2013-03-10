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
        return devices;
    }

    public static ArrayList<Device> getDevices() {
        if (devices == null) {
            devices = generateDevices();
        }
        return devices;
    }

}
