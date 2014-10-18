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

import android.net.Uri;
import com.f2prateek.dfg.model.Device;
import java.util.List;

public class Events {

  private Events() {
    // no instances
  }

  public static class GlareSettingUpdated {
    public final boolean isEnabled;

    public GlareSettingUpdated(boolean isEnabled) {
      this.isEnabled = isEnabled;
    }
  }

  public static class ShadowSettingUpdated {
    public final boolean isEnabled;

    public ShadowSettingUpdated(boolean isEnabled) {
      this.isEnabled = isEnabled;
    }
  }

  public static class DefaultDeviceUpdated {
    public final Device newDevice;

    public DefaultDeviceUpdated(Device newDevice) {
      this.newDevice = newDevice;
    }
  }

  public static class SingleImageProcessed {
    public final Device device;
    public final Uri uri;

    public SingleImageProcessed(Device device, Uri uri) {
      this.device = device;
      this.uri = uri;
    }
  }

  public static class MultipleImagesProcessed {
    public final Device device;
    public final List<Uri> uriList;

    public MultipleImagesProcessed(Device device, List<Uri> uriList) {
      this.device = device;
      this.uriList = uriList;
    }
  }
}
