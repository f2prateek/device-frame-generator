/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.util;

import android.os.Environment;

public class StorageUtils {

  /**
   * Checks if storage is available for our use.
   *
   * @return true if storage is available and writeable
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted") public static boolean isStorageAvailable() {
    boolean mExternalStorageAvailable;
    boolean mExternalStorageWriteable;
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      // We can read and write the media
      mExternalStorageAvailable = mExternalStorageWriteable = true;
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      // We can only read the media
      mExternalStorageAvailable = true;
      mExternalStorageWriteable = false;
    } else {
      // Something else is wrong. It may be one of many other states, but
      // all we need to know is we can neither read nor write
      mExternalStorageAvailable = mExternalStorageWriteable = false;
    }

    Ln.i(mExternalStorageAvailable ? "Storage available" : "Storage Unavailable");
    Ln.i(mExternalStorageWriteable ? "Storage writeable" : "Storage not writeable");

    return (mExternalStorageAvailable && mExternalStorageWriteable);
  }
}
