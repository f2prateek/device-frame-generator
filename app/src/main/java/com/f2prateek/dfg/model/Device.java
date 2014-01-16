/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {

  public static final String ORIENTATION_PORTRAIT = "port";
  public static final String ORIENTATION_LANDSCAPE = "land";

  // Unique identifier for each device, used to identify resources.
  private final String id;
  // Device name to display
  private final String name;
  // Device product URL
  private final String url;
  // Physical size of device, just for displaying to user
  private final float physicalSize;
  // DPI; just for displaying to user
  private final String density;
  // offset of screenshot from edges when in landscape
  private final int[] landOffset;
  // offset of screenshot from edges when in portrait
  private final int[] portOffset;
  // Screen resolution in portrait
  private final int[] portSize;
  // Screen resolution in portrait, that will be displayed to the user.
  // This may or may not be same as portSize
  private final int[] realSize;

  private Device(String id, String name, String url, float physicalSize, String density,
      int[] landOffset, int[] portOffset, int[] portSize, int[] realSize) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.physicalSize = physicalSize;
    this.density = density;
    this.landOffset = landOffset;
    this.portOffset = portOffset;
    this.portSize = portSize;
    this.realSize = realSize;
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

  public String getDensity() {
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

  public int[] getRealSize() {
    return realSize;
  }

  // Get the name of the shadow resource
  public String getShadowStringResourceName(String orientation) {
    return id + "_" + orientation + "_shadow";
  }

  // Get the name of the glare resource
  public String getGlareStringResourceName(String orientation) {
    return id + "_" + orientation + "_glare";
  }

  // Get the name of the background resource
  public String getBackgroundStringResourceName(String orientation) {
    return id + "_" + orientation + "_back";
  }

  // Get the name of the background resource
  public String getThumbnailResourceName() {
    return id + "_thumb";
  }

  @Override
  public String toString() {
    return "Device{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
  }

  protected Device(Parcel in) {
    id = in.readString();
    name = in.readString();
    url = in.readString();
    physicalSize = in.readFloat();
    density = in.readString();
    landOffset = new int[2];
    in.readIntArray(landOffset);
    portOffset = new int[2];
    in.readIntArray(portOffset);
    portSize = new int[2];
    in.readIntArray(portSize);
    realSize = new int[2];
    in.readIntArray(realSize);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(url);
    dest.writeFloat(physicalSize);
    dest.writeString(density);
    dest.writeIntArray(landOffset);
    dest.writeIntArray(portOffset);
    dest.writeIntArray(portSize);
    dest.writeIntArray(realSize);
  }

  public static final Creator<Device> CREATOR = new Creator<Device>() {
    public Device createFromParcel(Parcel in) {
      return new Device(in);
    }

    public Device[] newArray(int size) {
      return new Device[size];
    }
  };

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
      return new Device(id, name, url, physicalSize, density, landOffset, portOffset, portSize,
          realSize);
    }
  }
}
