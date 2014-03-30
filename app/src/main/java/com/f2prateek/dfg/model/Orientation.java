package com.f2prateek.dfg.model;

import android.graphics.Bitmap;

public enum Orientation {
  LANDSCAPE("land"), PORTRAIT("port");

  String id;

  private Orientation(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  /**
   * Checks if screenshot matches the aspect ratio of the device.
   */
  public static Orientation calculate(Bitmap screenshot, Device device) {
    Bounds screenshotBounds = Bounds.create(screenshot.getWidth(), screenshot.getHeight());
    return calculate(screenshotBounds, device.portSize());
  }

  /**
   * Check if the aspect ratio of the given bounds matches the device.
   */
  public static Orientation calculate(Bounds bounds, Device device) {
    return calculate(bounds, device.portSize());
  }

  /**
   * Check if the aspect ratios of the bounds match.
   *
   * @return {@link Orientation#PORTRAIT} if matched to portrait,
   * {@link Orientation#LANDSCAPE} if matched to landscape and null if no match
   */
  private static Orientation calculate(Bounds lhs, Bounds rhs) {
    float aspect1 = (float) lhs.y() / (float) lhs.x();
    float aspect2 = (float) rhs.y() / (float) rhs.x();

    if (aspect1 == aspect2) {
      return Orientation.PORTRAIT;
    } else if (aspect1 == 1 / aspect2) {
      return Orientation.LANDSCAPE;
    }

    return null;
  }
}
