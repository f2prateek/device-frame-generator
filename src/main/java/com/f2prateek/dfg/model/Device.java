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

package com.f2prateek.dfg.model;

import android.auto.value.AutoValue;
import android.os.Parcelable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@AutoValue public abstract class Device implements Parcelable {

  // Unique identifier for each device, also used to identify resources.
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
  public abstract Bounds landOffset();

  // offset of screenshot from edges when in portrait
  public abstract Bounds portOffset();

  // Screen resolution in portrait for the device frame
  public abstract Bounds portSize();

  // Screen resolution in portrait, that will be displayed to the user.
  // This may or may not be same as portSize
  public abstract Bounds realSize();

  // A list of product ids that match {@link android.os.Build#PRODUCT} for this device
  public abstract Collection<String> productIds();

  private static Device create(String id, String name, String url, float physicalSize,
      String density, Bounds landOffset, Bounds portOffset, Bounds portSize, Bounds realSize,
      Set<String> productIds) {
    return new AutoValue_Device(id, name, url, physicalSize, density, landOffset, portOffset,
        portSize, realSize, productIds);
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

  // Put all relevant values into the given container object and return it
  public void into(Map<String, Object> container) {
    container.put("device_id", id());
    container.put("device_name", name());
    container.put("device_bounds_x", portSize().x());
    container.put("device_bounds_y", portSize().y());
  }

  public static class Builder {

    private String id;
    private String name;
    private String url;
    private float physicalSize;
    private String density;
    private Bounds landOffset;
    private Bounds portOffset;
    private Bounds portSize;
    private Bounds realSize;
    private Set<String> productIds = new LinkedHashSet<>();

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

    public Builder setLandOffset(int landOffsetX, int landOffsetY) {
      this.landOffset = Bounds.create(landOffsetX, landOffsetY);
      return this;
    }

    public Builder setPortOffset(int portOffsetX, int portOffsetY) {
      this.portOffset = Bounds.create(portOffsetX, portOffsetY);
      return this;
    }

    public Builder setPortSize(int portSizeX, int portSizeY) {
      this.portSize = Bounds.create(portSizeX, portSizeY);
      return this;
    }

    public Builder setRealSize(int realSizeX, int realSizeY) {
      this.realSize = Bounds.create(realSizeX, realSizeY);
      return this;
    }

    public Builder addProductId(String id) {
      productIds.add(id);
      return this;
    }

    public Device build() {
      return create(id, name, url, physicalSize, density, landOffset, portOffset, portSize,
          realSize, productIds);
    }
  }
}
