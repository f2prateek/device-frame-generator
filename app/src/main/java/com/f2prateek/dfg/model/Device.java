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

public class Device {

    // Unique identifier for each device, used to identify resources.
    private String id;
    // Device name to display
    private String name;
    // Device product URL
    private String url;
    // Physical size of device, just for displaying to user
    private float physicalSize;
    // DPI; just for displaying to user
    private int density;
    // offset of screenshot from edges when in landscape
    private int[] landOffset;
    // offset of screenshot from edges when in portrait
    private int[] portOffset;
    // Screen resolution in portrait
    private int[] portSize;
    // Handle to resource for thumbnail
    private int thumbnail;

    public Device(String id, String name, String url, float physicalSize, int density, int[] landOffset, int[] portOffset, int[] portSize, int thumbnail) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.physicalSize = physicalSize;
        this.density = density;
        this.landOffset = landOffset;
        this.portOffset = portOffset;
        this.portSize = portSize;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public float getPhysicalSize() {
        return physicalSize;
    }

    public int getDensity() {
        return density;
    }

    public int[] getLandOffset() {
        return landOffset;
    }

    public int[] getPortOffset() {
        return portOffset;
    }

    public int[] getPortSize() {
        return portSize;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    // Get the name of the shadow resource
    public String getShadowString(String orientation) {
        return id + "_" + orientation + "_shadow";
    }

    // Get the name of the glare resource
    public String getGlareString(String orientation) {
        return id + "_" + orientation + "_glare";
    }

    // Get the name of the background resource
    public String getBackgroundString(String orientation) {
        return id + "_" + orientation + "_back";
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static class DeviceBuilder {
        private String id;
        private String name;
        private String url;
        private float physicalSize;
        private int density;
        private int[] landOffset;
        private int[] portOffset;
        private int[] portSize;
        private int thumbnail;

        public DeviceBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public DeviceBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DeviceBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public DeviceBuilder setPhysicalSize(float physicalSize) {
            this.physicalSize = physicalSize;
            return this;
        }

        public DeviceBuilder setDensity(int density) {
            this.density = density;
            return this;
        }

        public DeviceBuilder setLandOffset(int[] landOffset) {
            this.landOffset = landOffset;
            return this;
        }

        public DeviceBuilder setPortOffset(int[] portOffset) {
            this.portOffset = portOffset;
            return this;
        }

        public DeviceBuilder setPortSize(int[] portSize) {
            this.portSize = portSize;
            return this;
        }

        public DeviceBuilder setThumbnail(int thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Device build() {
            return new Device(id, name, url, physicalSize, density, landOffset, portOffset, portSize, thumbnail);
        }
    }
}
