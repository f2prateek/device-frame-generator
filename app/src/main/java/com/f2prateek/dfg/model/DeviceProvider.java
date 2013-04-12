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

    private static ArrayList<Device> devices;

    private static ArrayList<Device> generateDevices() {
        ArrayList<Device> devices = new ArrayList<Device>();
        devices.add(new Device.Builder()
                .setId("nexus_s")
                .setName("Nexus S")
                .setUrl("http://www.google.com/phone/detail/nexus-s")
                .setPhysicalSize(4.0f)
                .setDensity(240)
                .setLandOffset(new int[]{247, 135})
                .setPortOffset(new int[]{134, 247})
                .setPortSize(new int[]{480, 800})
                .setRealSize(new int[]{480, 800})
                .setThumbnail(R.drawable.nexus_s_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("galaxy_nexus")
                .setName("Galaxy Nexus")
                .setUrl("http://www.android.com/devices/detail/galaxy-nexus")
                .setPhysicalSize(4.65f)
                .setDensity(320)
                .setLandOffset(new int[]{371, 199})
                .setPortOffset(new int[]{216, 353})
                .setPortSize(new int[]{720, 1280})
                .setRealSize(new int[]{720, 1280})
                .setThumbnail(R.drawable.galaxy_nexus_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("nexus_4")
                .setName("Nexus 4")
                .setUrl("http://www.google.com/nexus/4/")
                .setPhysicalSize(4.7f)
                .setDensity(320)
                .setLandOffset(new int[]{349, 214})
                .setPortOffset(new int[]{213, 350})
                .setPortSize(new int[]{768, 1280})
                .setRealSize(new int[]{768, 1280})
                .setThumbnail(R.drawable.nexus_4_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("nexus_7")
                .setName("Nexus 7")
                .setUrl("http://www.android.com/devices/detail/nexus-7")
                .setPhysicalSize(7f)
                .setDensity(213)
                .setLandOffset(new int[]{315, 270})
                .setPortOffset(new int[]{264, 311})
                .setPortSize(new int[]{800, 1280})
                .setRealSize(new int[]{800, 1280})
                .setThumbnail(R.drawable.nexus_7_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("nexus_10")
                .setName("Nexus 10")
                .setUrl("http://www.google.com/nexus/10/")
                .setPhysicalSize(10f)
                .setDensity(320)
                .setLandOffset(new int[]{227, 217})
                .setPortOffset(new int[]{217, 223})
                .setPortSize(new int[]{800, 1280})
                .setRealSize(new int[]{1600, 2560})
                .setThumbnail(R.drawable.nexus_10_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("htc_one_x")
                .setName("HTC One X")
                .setUrl("http://www.htc.com/www/smartphones/htc-one-x/")
                .setPhysicalSize(4.7f)
                .setDensity(320)
                .setLandOffset(new int[]{346, 211})
                .setPortOffset(new int[]{302, 306})
                .setPortSize(new int[]{720, 1280})
                .setRealSize(new int[]{720, 1280})
                .setThumbnail(R.drawable.htc_one_x_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("samsung_galaxy_note")
                .setName("Samsung Galaxy Note")
                .setUrl("http://www.samsung.com/global/microsite/galaxynote/note/index.html")
                .setPhysicalSize(5.3f)
                .setDensity(320)
                .setLandOffset(new int[]{353, 209})
                .setPortOffset(new int[]{289, 312})
                .setPortSize(new int[]{800, 1280})
                .setRealSize(new int[]{800, 1280})
                .setThumbnail(R.drawable.samsung_galaxy_note_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("samsung_galaxy_s3")
                .setName("Samsung Galaxy S III")
                .setUrl("http://www.samsung.com/global/galaxys3/")
                .setPhysicalSize(4.8f)
                .setDensity(320)
                .setLandOffset(new int[]{346, 211})
                .setPortOffset(new int[]{302, 307})
                .setPortSize(new int[]{720, 1280})
                .setRealSize(new int[]{720, 1280})
                .setThumbnail(R.drawable.samsung_galaxy_s3_thumb)
                .build());
        devices.add(new Device.Builder().setId("samsung_galaxy_tab_2_7inch")
                .setName("Samsung Galaxy Tab 2")
                .setUrl("http://www.samsung.com/global/microsite/galaxytab2/7.0/index.html")
                .setPhysicalSize(7f).setDensity(160)
                .setLandOffset(new int[]{230, 203})
                .setPortOffset(new int[]{274, 222})
                .setPortSize(new int[]{600, 1024})
                .setRealSize(new int[]{600, 1024})
                .setThumbnail(R.drawable.samsung_galaxy_tab_2_7inch_thumb)
                .build());
        devices.add(new Device.Builder()
                .setId("xoom")
                .setName("Motorola XOOM")
                .setUrl("http://www.google.com/phone/detail/motorola-xoom")
                .setPhysicalSize(10f)
                .setDensity(160)
                .setLandOffset(new int[]{218, 191})
                .setPortOffset(new int[]{199, 200})
                .setPortSize(new int[]{800, 1280})
                .setRealSize(new int[]{800, 1280})
                .setThumbnail(R.drawable.xoom_thumb)
                .build());
        return devices;
    }

    public static ArrayList<Device> getDevices() {
        if (devices == null) {
            devices = generateDevices();
        }
        return devices;
    }

}
