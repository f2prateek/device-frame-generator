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

package com.f2prateek.dfg.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.crashlytics.android.Crashlytics;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.util.BitmapUtils;
import com.f2prateek.dfg.util.Ln;
import com.google.analytics.tracking.android.EasyTracker;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DeviceFrameGenerator {

  private final Context context;
  private final Callback callback;
  private final Device device;
  private final boolean withShadow;
  private final boolean withGlare;

  private DeviceFrameGenerator(Context context, Callback callback, Device device,
      boolean withShadow, boolean withGlare) {
    this.context = context;
    this.callback = callback;
    this.device = device;
    this.withShadow = withShadow;
    this.withGlare = withGlare;
  }

  public static void generate(Context context, Callback callback, Device device, boolean withShadow,
      boolean withGlare, Uri screenshotUri) {
    DeviceFrameGenerator generator =
        new DeviceFrameGenerator(context, callback, device, withShadow, withGlare);
    generator.generateFrame(screenshotUri);
  }

  /**
   * Checks if screenshot matches the aspect ratio of the device.
   *
   * @param device The Device to frame.
   * @param screenshot The screenshot to frame.
   * @return {@link Device#ORIENTATION_PORTRAIT} if matched to portrait and {@link
   * Device#ORIENTATION_LANDSCAPE} if matched to landscape, null if no match
   */
  private static String checkDimensions(Device device, Bitmap screenshot) {
    float aspect1 = (float) screenshot.getHeight() / (float) screenshot.getWidth();
    float aspect2 = (float) device.getPortSize()[1] / (float) device.getPortSize()[0];

    if (aspect1 == aspect2) {
      return Device.ORIENTATION_PORTRAIT;
    } else if (aspect1 == 1 / aspect2) {
      return Device.ORIENTATION_LANDSCAPE;
    }

    Ln.e("Screenshot height=%d, width=%d. Device height=%d, width=%d. Aspect1=%f, Aspect2=%f",
        screenshot.getHeight(), screenshot.getWidth(), device.getPortSize()[1],
        device.getPortSize()[0], aspect1, aspect2);
    return null;
  }

  /**
   * Check if the orientation is portrait
   *
   * @param orientation Orientation to check.
   * @return true if orientation is portrait
   */
  private static boolean isPortrait(String orientation) {
    return (orientation.compareTo("port") == 0);
  }

  /**
   * Generate the frame.
   *
   * @param screenshotUri Uri to the screenshot file.
   */
  private void generateFrame(Uri screenshotUri) {
    Ln.d("Generating for %s %s and %s from file %s.", device.getName(),
        withGlare ? " with glare " : " without glare ",
        withShadow ? " with shadow " : " without shadow ", screenshotUri);

    if (screenshotUri == null) {
      Resources r = context.getResources();
      callback.failedImage(r.getString(R.string.failed_open_screenshot_title),
          r.getString(R.string.no_image_received), r.getString(R.string.no_image_received));
      return;
    }

    try {
      Bitmap screenshot = BitmapUtils.decodeUri(context.getContentResolver(), screenshotUri);
      if (screenshot != null) {
        generateFrame(screenshot);
      } else {
        failedToOpenScreenshot(screenshotUri);
      }
    } catch (IOException e) {
      failedToOpenScreenshot(screenshotUri);
      Crashlytics.logException(e);
    }
  }

  private void failedToOpenScreenshot(Uri screenshotUri) {
    Resources r = context.getResources();
    callback.failedImage(r.getString(R.string.failed_open_screenshot_title),
        r.getString(R.string.failed_open_screenshot_text, screenshotUri.toString()),
        r.getString(R.string.failed_open_screenshot_text, screenshotUri.toString()));
  }

  /**
   * Generate the frame.
   *
   * @param screenshot Screenshot to use.
   */
  private void generateFrame(Bitmap screenshot) {
    callback.startingImage(screenshot);
    String orientation;
    orientation = checkDimensions(device, screenshot);
    if (orientation == null) {
      Resources r = context.getResources();
      String failedTitle = r.getString(R.string.failed_match_dimensions_title);
      String failedText =
          r.getString(R.string.failed_match_dimensions_text, device.getPortSize()[0],
              device.getPortSize()[1], screenshot.getHeight(), screenshot.getWidth());
      String failedSmallText = r.getString(R.string.device_chosen, device.getName());
      callback.failedImage(failedTitle, failedSmallText, failedText);
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("incorrect_dimensions", device.getId());
      EasyTracker.getInstance(context).send(params);
      return;
    }

    final Bitmap background =
        BitmapUtils.decodeResource(context, device.getBackgroundStringResourceName(orientation));
    final Bitmap glare =
        BitmapUtils.decodeResource(context, device.getGlareStringResourceName(orientation));
    final Bitmap shadow =
        BitmapUtils.decodeResource(context, device.getShadowStringResourceName(orientation));

    Canvas frame;
    if (withShadow) {
      frame = new Canvas(shadow);
      frame.drawBitmap(background, 0f, 0f, null);
    } else {
      frame = new Canvas(background);
    }

    final int[] offset;
    if (isPortrait(orientation)) {
      screenshot =
          Bitmap.createScaledBitmap(screenshot, device.getPortSize()[0], device.getPortSize()[1],
              false);
      offset = device.getPortOffset();
    } else {
      screenshot =
          Bitmap.createScaledBitmap(screenshot, device.getPortSize()[1], device.getPortSize()[0],
              false);
      offset = device.getLandOffset();
    }
    frame.drawBitmap(screenshot, offset[0], offset[1], null);

    if (withGlare) {
      frame.drawBitmap(glare, 0f, 0f, null);
    }

    ImageMetadata imageMetadata = prepareMetadata();
    // Save the screenshot to the MediaStore
    ContentValues values = new ContentValues();
    ContentResolver resolver = context.getContentResolver();
    values.put(MediaStore.Images.ImageColumns.DATA, imageMetadata.imageFilePath);
    values.put(MediaStore.Images.ImageColumns.TITLE, imageMetadata.imageFileName);
    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageMetadata.imageFileName);
    values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageMetadata.imageTime);
    values.put(MediaStore.Images.ImageColumns.DATE_ADDED, imageMetadata.imageTime);
    values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, imageMetadata.imageTime);
    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
    values.put(MediaStore.Images.ImageColumns.WIDTH, background.getWidth());
    values.put(MediaStore.Images.ImageColumns.HEIGHT, background.getHeight());
    Uri frameUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    try {
      OutputStream out = resolver.openOutputStream(frameUri);
      if (withShadow) {
        shadow.compress(Bitmap.CompressFormat.PNG, 100, out);
      } else {
        background.compress(Bitmap.CompressFormat.PNG, 100, out);
      }
      out.flush();
      out.close();
    } catch (IOException e) {
      Ln.e(e);
      Resources r = context.getResources();
      callback.failedImage(r.getString(R.string.unknown_error_title),
          r.getString(R.string.unknown_error_text), r.getString(R.string.unknown_error_text));
      return;
    } finally {
      screenshot.recycle();
      background.recycle();
      glare.recycle();
      shadow.recycle();
    }

    // update file size in the database
    values.clear();
    values.put(MediaStore.Images.ImageColumns.SIZE, new File(imageMetadata.imageFilePath).length());
    resolver.update(frameUri, values, null, null);

    Ln.d("Generated for %s at %s with uri %s", device.getName(), imageMetadata.imageFilePath,
        frameUri);

    callback.doneImage(frameUri);
  }

  /**
   * Prepare the metadata for our image.
   *
   * @return {@link ImageMetadata} that will be used for the image.
   */
  private ImageMetadata prepareMetadata() {
    ImageMetadata imageMetadata = new ImageMetadata();
    imageMetadata.imageTime = System.currentTimeMillis();
    String imageDate =
        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(imageMetadata.imageTime));
    String imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        .getAbsolutePath();
    File dfgDir = new File(imageDir, AppConstants.DFG_DIR_NAME);
    dfgDir.mkdirs();
    imageMetadata.imageFileName = String.format(AppConstants.DFG_FILE_NAME_TEMPLATE, imageDate);
    imageMetadata.imageFilePath = new File(dfgDir, imageMetadata.imageFileName).getAbsolutePath();
    return imageMetadata;
  }

  // Views should have these methods to notify the user.
  public interface Callback {
    void startingImage(Bitmap screenshot);

    void failedImage(String title, String smallText, String text);

    void doneImage(Uri imageUri);
  }

  public class ImageMetadata {
    String imageFileName;
    String imageFilePath;
    long imageTime;
  }
}