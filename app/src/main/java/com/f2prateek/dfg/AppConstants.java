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

package com.f2prateek.dfg;

public class AppConstants {

  /*
   * Key for preferences; saves default device
   */
  public static final String KEY_PREF_DEFAULT_DEVICE = "KEY_PREF_DEFAULT_DEVICE";
  public static final String KEY_PREF_OPTION_GLARE = "KEY_PREF_OPTION_GLARE";
  public static final String KEY_PREF_OPTION_SHADOW = "KEY_PREF_OPTION_SHADOW";
  /*
   * Key for Intent extras;
   */
  public static final String KEY_EXTRA_DEVICE = "KEY_EXTRA_DEVICE";
  public static final String KEY_EXTRA_SCREENSHOT = "KEY_EXTRA_SCREENSHOT";
  public static final String KEY_EXTRA_SCREENSHOTS = "KEY_EXTRA_SCREENSHOTS";
  /*
   * Storage
   */
  public static final String DFG_DIR_NAME = "Device-Frame-Generator";
  public static final String DFG_FILE_NAME_TEMPLATE = "DFG_%s.png";
}
