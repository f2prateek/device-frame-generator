/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.f2prateek.dfg;

import com.f2prateek.dfg.model.Device;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import java.util.Set;

import static dagger.Provides.Type.SET;
import static dagger.Provides.Type.SET_VALUES;

@Module(library = true)
public class DeviceModule {

  @Provides(type = SET_VALUES) Set<Device> provideEmptyDevices() {
    return new HashSet<>(); // Empty set to ensure the Set is initialized.
  }

  @Provides(type = SET) Device provideNexusS() {
    return new Device.Builder().setId("nexus_s")
        .setName("Nexus S")
        .setUrl("http://www.google.com/phone/detail/nexus-s")
        .setPhysicalSize(4.0f)
        .setDensity("HDPI")
        .setLandOffset(247, 135)
        .setPortOffset(134, 247)
        .setPortSize(480, 800)
        .setRealSize(480, 800)
        .addProductId("soju")
        .addProductId("sojua")
        .addProductId("sojuk")
        .addProductId("sojus")
        .build();
  }

  @Provides(type = SET) Device provideGalaxyNexus() {
    return new Device.Builder().setId("galaxy_nexus")
        .setName("Galaxy Nexus")
        .setUrl("http://www.android.com/devices/detail/galaxy-nexus")
        .setPhysicalSize(4.65f)
        .setDensity("XHDPI")
        .setLandOffset(371, 199)
        .setPortOffset(216, 353)
        .setPortSize(720, 1280)
        .setRealSize(720, 1280)
        .addProductId("takju")
        .addProductId("yakju")
        .addProductId("mysid")
        .addProductId("mysidspr")
        .build();
  }

  @Provides(type = SET) Device provideNexus4() {
    return new Device.Builder().setId("nexus_4")
        .setName("Nexus 4")
        .setUrl("http://www.google.com/nexus/4/")
        .setPhysicalSize(4.7f)
        .setDensity("XHDPI")
        .setLandOffset(349, 214)
        .setPortOffset(213, 350)
        .setPortSize(768, 1280)
        .setRealSize(768, 1280)
        .addProductId("occam")
        .build();
  }

  @Provides(type = SET) Device provideNexus5() {
    return new Device.Builder().setId("nexus_5")
        .setName("Nexus 5")
        .setUrl("http://www.google.com/nexus/5/")
        .setPhysicalSize(5.43f)
        .setDensity("XXHDPI")
        .setLandOffset(436, 306)
        .setPortOffset(306, 436)
        .setPortSize(1080, 1920)
        .setRealSize(1080, 1920)
        .addProductId("hammerhead")
        .build();
  }

  @Provides(type = SET) Device provideNexus72013() {
    return new Device.Builder().setId("nexus_7_2013")
        .setName("Nexus 7 (2013)")
        .setUrl("http://www.google.com/nexus/7/")
        .setPhysicalSize(8f)
        .setDensity("XHDPI")
        .setLandOffset(326, 245)
        .setPortOffset(244, 326)
        .setPortSize(800, 1280)
        .setRealSize(1200, 1920)
        .addProductId("razor")
        .addProductId("razorg")
        .build();
  }

  @Provides(type = SET) Device provideNexus7() {
    return new Device.Builder().setId("nexus_7")
        .setName("Nexus 7")
        .setUrl("http://www.android.com/devices/detail/nexus-7")
        .setPhysicalSize(7f)
        .setDensity("213 dpi")
        .setLandOffset(315, 270)
        .setPortOffset(264, 311)
        .setPortSize(800, 1280)
        .setRealSize(800, 1280)
        .addProductId("nakasi")
        .addProductId("nakasig")
        .build();
  }

  @Provides(type = SET) Device provideNexus10() {
    return new Device.Builder().setId("nexus_10")
        .setName("Nexus 10")
        .setUrl("http://www.google.com/nexus/10/")
        .setPhysicalSize(10f)
        .setDensity("XHDPI")
        .setLandOffset(227, 217)
        .setPortOffset(217, 223)
        .setPortSize(800, 1280)
        .setRealSize(1600, 2560)
        .addProductId("mantaray")
        .build();
  }

  @Provides(type = SET) Device provideHtcOneX() {
    return new Device.Builder().setId("htc_one_x")
        .setName("HTC One X")
        .setUrl("http://www.htc.com/www/smartphones/htc-one-x/")
        .setPhysicalSize(4.7f)
        .setDensity("320 dpi")
        .setLandOffset(346, 211)
        .setPortOffset(302, 306)
        .setPortSize(720, 1280)
        .setRealSize(720, 1280)
        .build();
  }

  @Provides(type = SET) Device provideSamsungGalaxyNote() {
    return new Device.Builder().setId("samsung_galaxy_note")
        .setName("Samsung Galaxy Note")
        .setUrl("http://www.samsung.com/global/microsite/galaxynote/note/index.html")
        .setPhysicalSize(5.3f)
        .setDensity("320 dpi")
        .setLandOffset(353, 209)
        .setPortOffset(289, 312)
        .setPortSize(800, 1280)
        .setRealSize(800, 1280)
        .build();
  }

  @Provides(type = SET) Device provideHtcM7() {
    return new Device.Builder().setId("htc_m7")
        .setName("HTC One")
        .setUrl("http://www.htc.com/www/smartphones/htc-one/")
        .setPhysicalSize(4.7f)
        .setDensity("468 dpi")
        .setLandOffset(624, 324)
        .setPortOffset(324, 624)
        .setPortSize(1080, 1920)
        .setRealSize(1080, 1920)
        .build();
  }

  @Provides(type = SET) Device provideSamsungGalaxyS3() {
    return new Device.Builder().setId("samsung_galaxy_s3")
        .setName("Samsung Galaxy S III")
        .setUrl("http://www.samsung.com/global/galaxys3/")
        .setPhysicalSize(4.8f)
        .setDensity("320 dpi")
        .setLandOffset(346, 211)
        .setPortOffset(302, 307)
        .setPortSize(720, 1280)
        .setRealSize(720, 1280)
        .build();
  }

  @Provides(type = SET) Device provideSamsungGalaxyTab27Inch() {
    return new Device.Builder().setId("samsung_galaxy_tab_2_7inch")
        .setName("Samsung Galaxy Tab 2")
        .setUrl("http://www.samsung.com/global/microsite/galaxytab2/7.0/index.html")
        .setPhysicalSize(7f)
        .setDensity("160 dpi")
        .setLandOffset(230, 203)
        .setPortOffset(274, 222)
        .setPortSize(600, 1024)
        .setRealSize(600, 1024)
        .build();
  }

  @Provides(type = SET) Device provideXperiaZ1() {
    return new Device.Builder().setId("xperia_z1")
        .setName("Xperia Z1")
        .setUrl("http://www.sonymobile.com/us/products/phones/xperia-z1/")
        .setPhysicalSize(5.0f)
        .setDensity("XHDPI")
        .setLandOffset(340, 208)
        .setPortOffset(221, 340)
        .setPortSize(720, 1280)
        .setRealSize(1080, 1920)
        .build();
  }

  @Provides(type = SET) Device provideXoom() {
    return new Device.Builder().setId("xoom")
        .setName("Motorola XOOM")
        .setUrl("http://www.google.com/phone/detail/motorola-xoom")
        .setPhysicalSize(10f)
        .setDensity("MDPI")
        .setLandOffset(218, 191)
        .setPortOffset(199, 200)
        .setPortSize(800, 1280)
        .setRealSize(800, 1280)
        .build();
  }
}
