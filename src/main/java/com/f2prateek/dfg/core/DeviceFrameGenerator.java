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

package com.f2prateek.dfg.core;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.Utils;
import com.f2prateek.dfg.model.Bounds;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.Orientation;
import com.f2prateek.dfg.prefs.BackgroundColorOption;
import com.f2prateek.ln.Ln;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.f2prateek.dfg.Utils.createDirectory;
import static com.f2prateek.dfg.Utils.cropBitmap;
import static com.f2prateek.dfg.Utils.recycleBitmap;
import static com.f2prateek.dfg.Utils.scaleBitmapDown;

public class DeviceFrameGenerator {

  @SuppressLint("SimpleDateFormat") private static final DateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
  private final Context context;
  private final Callback callback;
  private final Device device;
  private final boolean shadowEnabled;
  private final boolean glareEnabled;
  private final boolean colorBackgroundEnabled;
  private final boolean blurBackgroundEnabled;
  private final BackgroundColorOption.Option backgroundColorOption;
  private final int customBackgroundColor;
  private final int backgroundPaddingPercentage;
  private final int backgroundBlurRadius;

  public DeviceFrameGenerator(Context context, Callback callback, Device device,
      boolean shadowEnabled, boolean glareEnabled, boolean colorBackgroundEnabled,
      boolean blurBackgroundEnabled, BackgroundColorOption.Option backgroundColorOption,
      int customBackgroundColor, int backgroundPaddingPercentage, int backgroundBlurRadius) {
    this.backgroundPaddingPercentage = backgroundPaddingPercentage;
    this.backgroundBlurRadius = backgroundBlurRadius;
    this.context = context.getApplicationContext();
    this.callback = callback;
    this.device = device;
    this.shadowEnabled = shadowEnabled;
    this.glareEnabled = glareEnabled;
    this.colorBackgroundEnabled = colorBackgroundEnabled;
    this.blurBackgroundEnabled = blurBackgroundEnabled;
    this.backgroundColorOption = backgroundColorOption;
    this.customBackgroundColor = customBackgroundColor;
  }

  /**
   * Generate the frame.
   *
   * @param screenshotUri Uri to the screenshot file.
   */
  public void generateFrame(Uri screenshotUri) {
    Ln.d("Generating for %s %s and %s from uri %s.", device.name(),
        glareEnabled ? " with glare " : " without glare ",
        shadowEnabled ? " with shadow " : " without shadow ", screenshotUri);

    if (screenshotUri == null) {
      Resources r = context.getResources();
      callback.failedImage(r.getString(R.string.failed_open_screenshot_title),
          r.getString(R.string.no_image_received), null);
      return;
    }

    try {
      Bitmap screenshot = Utils.decodeUri(context.getContentResolver(), screenshotUri);
      if (screenshot != null) {
        generateFrame(screenshot);
      } else {
        Ln.e("failed to open the screenshot.");
        failedToOpenScreenshot(screenshotUri);
      }
    } catch (IOException e) {
      Ln.e(e, "failed to open the screenshot.");
      failedToOpenScreenshot(screenshotUri);
    }
  }

  private void failedToOpenScreenshot(Uri screenshotUri) {
    Resources r = context.getResources();
    callback.failedImage(r.getString(R.string.failed_open_screenshot_title),
        r.getString(R.string.failed_open_screenshot_text, screenshotUri.toString()), null);
  }

  /**
   * Generate the frame.
   *
   * @param screenshot non-null screenshot to use.
   */
  void generateFrame(Bitmap screenshot) {
    callback.startingImage(screenshot);
    Orientation orientation;
    orientation = Orientation.calculate(screenshot, device);
    if (orientation == null) {
      Ln.e("Could not match dimensions to the device.");
      Resources r = context.getResources();
      callback.failedImage(r.getString(R.string.failed_match_dimensions_title),
          r.getString(R.string.failed_match_dimensions_text, device.portSize().x(),
              device.portSize().y(), screenshot.getHeight(), screenshot.getWidth()),
          r.getString(R.string.device_chosen, device.name()));
      return;
    }

    // Calculate the offset for the screenshot depending on the orientation, and scale if necessary
    Bounds offset;
    if (orientation == Orientation.PORTRAIT) {
      screenshot =
          Bitmap.createScaledBitmap(screenshot, device.portSize().x(), device.portSize().y(),
              false);
      offset = device.portOffset();
    } else {
      screenshot =
          Bitmap.createScaledBitmap(screenshot, device.portSize().y(), device.portSize().x(),
              false);
      offset = device.landOffset();
    }

    // Get the frame for the orientation
    Bitmap frame =
        Utils.decodeResource(context, device.getBackgroundStringResourceName(orientation.getId()));

    // Offsets to account for generated background padding
    float leftOffset = 0f;
    float topOffset = 0f;
    // Generated bitmap dimensions w/ padding
    int generatedBitmapWidth = frame.getWidth();
    int generatedBitmapHeight = frame.getHeight();
    if (colorBackgroundEnabled || blurBackgroundEnabled) {
      leftOffset = frame.getWidth() * backgroundPaddingPercentage / 100;
      topOffset = frame.getHeight() * backgroundPaddingPercentage / 100;
      generatedBitmapWidth += (leftOffset * 2);
      generatedBitmapHeight += (topOffset * 2);
    }

    // Generate a bitmap to draw into
    Bitmap generatedBitmap =
        Bitmap.createBitmap(generatedBitmapWidth, generatedBitmapHeight, Bitmap.Config.ARGB_8888);
    Canvas generatedCanvas = new Canvas(generatedBitmap);

    // Draw the background
    if (colorBackgroundEnabled) {
      int color = getBackgroundColor(screenshot);
      Ln.d("Using background color %s.", color);
      generatedCanvas.drawColor(color);
    } else if (blurBackgroundEnabled) {
      drawBlur(generatedCanvas, screenshot, generatedBitmap);
    }

    // Draw the shadow if enabled
    Bitmap shadow = null;
    if (shadowEnabled) {
      shadow =
          Utils.decodeResource(context, device.getShadowStringResourceName(orientation.getId()));
      generatedCanvas.drawBitmap(shadow, leftOffset, topOffset, null);
    }

    // Draw the frame
    generatedCanvas.drawBitmap(frame, leftOffset, topOffset, null);

    // Draw the screenshot
    generatedCanvas.drawBitmap(screenshot, leftOffset + offset.x(), topOffset + offset.y(), null);

    // Draw the glare if enabled
    Bitmap glare = null;
    if (glareEnabled) {
      glare = Utils.decodeResource(context, device.getGlareStringResourceName(orientation.getId()));
      generatedCanvas.drawBitmap(glare, leftOffset, topOffset, null);
    }

    // Prepare data about the image
    ImageMetadata imageMetadata = prepareMetadata();
    ContentValues values = new ContentValues();
    ContentResolver resolver = context.getContentResolver();
    imageMetadata.copyTo(values);
    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      values.put(MediaStore.Images.ImageColumns.WIDTH, frame.getWidth());
      values.put(MediaStore.Images.ImageColumns.HEIGHT, frame.getHeight());
    }

    // Save the screenshot to the MediaStore
    Uri frameUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    try {
      if (frameUri == null) {
        throw new IOException("Content Resolved could not save image");
      }
      OutputStream out = resolver.openOutputStream(frameUri);
      generatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
      out.flush();
      out.close();
    } catch (IOException e) {
      Ln.e(e, "IOException when saving image.");
      Resources r = context.getResources();
      callback.failedImage(r.getString(R.string.unknown_error_title),
          r.getString(R.string.unknown_error_text), null);
      return;
    } finally {
      recycleBitmap(screenshot);
      recycleBitmap(frame);
      recycleBitmap(glare);
      recycleBitmap(shadow);
      recycleBitmap(generatedBitmap);
    }

    // Update file size in the database
    values.clear();
    values.put(MediaStore.Images.ImageColumns.SIZE, new File(imageMetadata.imageFilePath).length());
    resolver.update(frameUri, values, null, null);

    Ln.d("Generated for %s at %s with uri %s", device.name(), imageMetadata.imageFilePath,
        frameUri);
    callback.doneImage(frameUri);
  }

  private void drawBlur(Canvas canvas, Bitmap screenshot, Bitmap generatedBitmap) {
    Bitmap downscaledScreenshot = scaleBitmapDown(screenshot, 250);
    Bitmap croppedScreenshot = cropBitmap(downscaledScreenshot, 25);
    // Recycle the temporary downscaled screenshot. This check is probably never true for
    // the images we work with.
    if (downscaledScreenshot != screenshot) {
      recycleBitmap(downscaledScreenshot);
    }

    // Create an empty bitmap with the same size of the bitmap we want to blur
    Bitmap blurredScreenshot =
        Bitmap.createBitmap(croppedScreenshot.getWidth(), croppedScreenshot.getHeight(),
            Bitmap.Config.ARGB_8888);

    // Instantiate a new Renderscript
    RenderScript renderScript = RenderScript.create(context);

    // Create an Intrinsic Blur Script using the Renderscript
    ScriptIntrinsicBlur blurScript =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
    blurScript.setRadius(backgroundBlurRadius);

    // Create the in/out Allocations with the Renderscript and the in/out bitmaps
    Allocation allIn = Allocation.createFromBitmap(renderScript, croppedScreenshot);
    Allocation allOut = Allocation.createFromBitmap(renderScript, blurredScreenshot);

    // Perform the Renderscript
    blurScript.setInput(allIn);
    blurScript.forEach(allOut);

    // Copy the final bitmap created by the out Allocation to the blurredScreenshot
    allOut.copyTo(blurredScreenshot);

    // Draw the blurred screenshot into our canvas
    Rect bounds = new Rect();
    bounds.set(0, 0, generatedBitmap.getWidth(), generatedBitmap.getHeight());
    canvas.drawBitmap(blurredScreenshot, null, bounds, null);

    // After finishing everything, we destroy the Renderscript.
    renderScript.destroy();
  }

  int getBackgroundColor(Bitmap screenshot) {
    if (backgroundColorOption == BackgroundColorOption.Option.CUSTOM) {
      return customBackgroundColor;
    } else {
      Palette palette = Palette.generate(screenshot);
      switch (backgroundColorOption) {
        case VIBRANT:
          return palette.getVibrantColor(customBackgroundColor);
        case VIBRANT_DARK:
          return palette.getDarkVibrantColor(customBackgroundColor);
        case VIBRANT_LIGHT:
          return palette.getLightVibrantColor(customBackgroundColor);
        case MUTED:
          return palette.getMutedColor(customBackgroundColor);
        case MUTED_DARK:
          return palette.getDarkMutedColor(customBackgroundColor);
        case MUTED_LIGHT:
          return palette.getLightMutedColor(customBackgroundColor);
        default:
          throw new IllegalArgumentException("Unhandled color option");
      }
    }
  }

  /**
   * Prepare the metadata for our image.
   *
   * @return {@link ImageMetadata} that will be used for the image.
   */
  ImageMetadata prepareMetadata() {
    ImageMetadata imageMetadata = new ImageMetadata();
    imageMetadata.imageTime = System.currentTimeMillis();
    String imageDate = DATE_FORMAT.format(new Date(imageMetadata.imageTime));
    File dfgDir = createFramesFolder();
    imageMetadata.imageFileName = String.format(AppConstants.DFG_FILE_NAME_TEMPLATE, imageDate);
    imageMetadata.imageFilePath = new File(dfgDir, imageMetadata.imageFileName).getAbsolutePath();
    return imageMetadata;
  }

  File createFramesFolder() {
    File dfgDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .getAbsolutePath(), AppConstants.DFG_DIR_NAME);
    try {
      createDirectory(dfgDir);
    } catch (IOException e) {
      Ln.e(e);
      throw new AssertionError("Could not create folder " + dfgDir);
    }
    return dfgDir;
  }

  // Views should have these methods to notify the user.
  public interface Callback {

    void startingImage(Bitmap screenshot);

    void failedImage(String title, String text, String extra);

    void doneImage(Uri imageUri);
  }

  public class ImageMetadata {
    String imageFileName;
    String imageFilePath;
    long imageTime;

    void copyTo(ContentValues values) {
      values.put(MediaStore.Images.ImageColumns.DATA, imageFilePath);
      values.put(MediaStore.Images.ImageColumns.TITLE, imageFileName);
      values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageFileName);
      values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageTime);
      values.put(MediaStore.Images.ImageColumns.DATE_ADDED, imageTime);
      values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, imageTime);
    }
  }
}