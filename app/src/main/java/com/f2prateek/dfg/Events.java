package com.f2prateek.dfg;

import com.f2prateek.dfg.model.Device;

/**
 * Copyright 2013 prateek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Events {

    private Events() {
        // no instances
    }

    public static class GlareSettingUpdated {
        public boolean isEnabled;

        public GlareSettingUpdated(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public static class ShadowSettingUpdated {
        public boolean isEnabled;

        public ShadowSettingUpdated(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public static class DefaultDeviceUpdated {
        public int newDevice;

        public DefaultDeviceUpdated(int newDevice) {
            this.newDevice = newDevice;
        }
    }

    public static class SingleImageProcessed {
        public Device device;

        public SingleImageProcessed(Device device) {
            this.device = device;
        }
    }

    public static class MultipleImagesProcessed {
        public Device device;
        public int count;

        public MultipleImagesProcessed(Device device, int count) {
            this.device = device;
            this.count = count;
        }
    }

}
