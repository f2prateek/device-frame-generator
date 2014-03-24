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

package com.f2prateek.dfg.model;

import android.auto.value.AutoValue;
import android.os.Parcelable;

@AutoValue
public abstract class Device implements Parcelable {

  public static final String ORIENTATION_PORTRAIT = "port";
  public static final String ORIENTATION_LANDSCAPE = "land";

  // Unique identifier for each device, used to identify resources.
  public abstract String id();

  // Device name to display
  public abstract String name();

  // Device product URL
  public abstract String url();

  // Physical size of device, just for displaying to user
  public abstract float physicalSize();

  // DPI; just for displaying to user
  public abstract String density();

  // offset of screenshot from edges when in landscape
  public abstract int[] landOffset();

  // offset of screenshot from edges when in portrait
  public abstract int[] portOffset();

  // Screen resolution in portrait
  public abstract int[] portSize();

  // Screen resolution in portrait, that will be displayed to the user.
  // This may or may not be same as portSize
  public abstract int[] realSize();

  static Device create(String id, String name, String url, float physicalSize, String density,
      int[] landOffset, int[] portOffset, int[] portSize, int[] realSize) {
    return new AutoValue_Device(id, name, url, physicalSize, density, landOffset, portOffset,
        portSize, realSize);
  }

  // Get the name of the shadow resource
  public String getShadowStringResourceName(String orientation) {
    return id() + "_" + orientation + "_shadow";
  }

  // Get the name of the glare resource
  public String getGlareStringResourceName(String orientation) {
    return id() + "_" + orientation + "_glare";
  }

  // Get the name of the background resource
  public String getBackgroundStringResourceName(String orientation) {
    return id() + "_" + orientation + "_back";
  }

  // Get the name of the thumbnail resource
  public String getThumbnailResourceName() {
    return id() + "_thumb";
  }

  public static class Builder {
    private String id;
    private String name;
    private String url;
    private float physicalSize;
    private String density;
    private int[] landOffset;
    private int[] portOffset;
    private int[] portSize;
    private int[] realSize;

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setPhysicalSize(float physicalSize) {
      this.physicalSize = physicalSize;
      return this;
    }

    public Builder setDensity(String density) {
      this.density = density;
      return this;
    }

    public Builder setLandOffset(int[] landOffset) {
      this.landOffset = landOffset;
      return this;
    }

    public Builder setPortOffset(int[] portOffset) {
      this.portOffset = portOffset;
      return this;
    }

    public Builder setPortSize(int[] portSize) {
      this.portSize = portSize;
      return this;
    }

    public Builder setRealSize(int[] realSize) {
      this.realSize = realSize;
      return this;
    }

    public Device build() {
      return create(id, name, url, physicalSize, density, landOffset, portOffset, portSize,
          realSize);
    }
  }
}
