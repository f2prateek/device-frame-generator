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

import android.annotation.TargetApi;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.f2prateek.dfg.model.Bounds;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.Orientation;
import com.f2prateek.ln.Ln;
import com.f2prateek.rx.preferences.Preference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeviceProvider {
  private final Map<String, Device> deviceMap;
  private final Preference<String> defaultDevice;
  private List<Device> deviceList;

  private DeviceProvider(Map<String, Device> deviceMap, Preference<String> defaultDevice) {
    this.deviceMap = deviceMap;
    this.defaultDevice = defaultDevice;
  }

  public static DeviceProvider fromSet(Set<Device> deviceSet, Preference<String> defaultDevice) {
    HashMap<String, Device> deviceMap = new LinkedHashMap<>(deviceSet.size());
    for (Device device : deviceSet) {
      deviceMap.put(device.id(), device);
    }
    return new DeviceProvider(deviceMap, defaultDevice);
  }

  /**
   * Look up a given map of devices to find a device with a matching id.
   * Ideally we would have devices id'd by product name (e.g. crespo instead of nexus_s), but a
   * device could have multiple flavours (e.g. crespo and crespo4g), which will require iteration.
   *
   * @param productId the device id to look for
   * @return the device if found, null otherwise
   */
  private Device findByProductId(String productId) {
    for (Device device : deviceMap.values()) {
      if (device.productIds().contains(productId)) {
        return device;
      }
    }
    Ln.w("No device found with id = %s", productId);
    return null;
  }

  /**
   * Look up a given map of devices to find a device with matching bounds.
   * Bounds are matched if the aspect ratio of the device matches the aspect ratio of the bounds.
   *
   * @param bounds the bounds that the device should match
   * @return the device with matching bounds if found, null otherwise
   */
  private Device findByBounds(Bounds bounds) {
    for (Device device : deviceMap.values()) {
      if (Orientation.calculate(bounds, device) != null) {
        return device;
      }
    }
    Ln.w("No device found with bounds = %s", bounds);
    return null;
  }

  /**
   * Find a device that matches the {@link android.os.Build#PRODUCT} of the current device, or the
   * dimensions of the default display of the window manger.
   *
   * Returns null if not found.
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public Device find(WindowManager windowManager) {
    // look up devices by {@link android.os.Build#PRODUCT} value
    Device device = findByProductId(Build.PRODUCT);
    if (device != null) {
      return device;
    }

    // Couldn't find by product name, so look by dimensions of device
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      // The following API is only on 17+ so only do it if available
      DisplayMetrics metrics = new DisplayMetrics();
      windowManager.getDefaultDisplay().getRealMetrics(metrics);
      Bounds screenBounds = Bounds.create(metrics.heightPixels, metrics.widthPixels);
      device = findByBounds(screenBounds);
      if (device != null) {
        return device;
      }
    }
    return null;
  }

  /**
   * Save a default device to the user's preferences.
   */
  public void saveDefaultDevice(Device device) {
    defaultDevice.set(device.id());
  }

  /**
   * Get the user's default device.
   */
  public Device getDefaultDevice() {
    return deviceMap.get(defaultDevice.get());
  }

  /**
   * Return a device with the given id.
   */
  public Device get(String id) {
    return deviceMap.get(id);
  }

  public List<Device> asList() {
    if (deviceList == null) {
      deviceList = new ArrayList<>(deviceMap.values());
    }
    return deviceList;
  }
}
