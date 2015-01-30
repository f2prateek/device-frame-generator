/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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



}