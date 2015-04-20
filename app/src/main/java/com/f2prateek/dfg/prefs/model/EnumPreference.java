package com.f2prateek.dfg.prefs.model;

import android.content.SharedPreferences;

public class EnumPreference<T extends Enum<T>> {
  private final SharedPreferences preferences;
  private final String key;
  private final T defaultValue;
  private final Class<T> enumClass;

  public EnumPreference(SharedPreferences preferences, String key, T defaultValue,
      Class<T> enumClass) {
    this.preferences = preferences;
    this.key = key;
    this.defaultValue = defaultValue;
    this.enumClass = enumClass;
  }

  public T get() {
    String serialized = preferences.getString(key, defaultValue.toString());
    if (serialized == null) {
      throw new AssertionError("default value must be provided");
    }
    return Enum.valueOf(enumClass, serialized);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(T value) {
    preferences.edit().putString(key, value.toString()).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }
}
