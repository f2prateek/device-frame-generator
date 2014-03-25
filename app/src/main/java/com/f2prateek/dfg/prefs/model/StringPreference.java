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

package com.f2prateek.dfg.prefs.model;

import android.content.SharedPreferences;

public class StringPreference {
  private final SharedPreferences preferences;
  private final String key;
  private final String defaultValue;

  public StringPreference(SharedPreferences preferences, String key) {
    this(preferences, key, null);
  }

  public StringPreference(SharedPreferences preferences, String key, String defaultValue) {
    this.preferences = preferences;
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public String get() {
    return preferences.getString(key, defaultValue);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(String value) {
    preferences.edit().putString(key, value).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }
}
