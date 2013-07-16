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

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ServiceTestCase;
import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.Ln;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

public class GenerateFrameServiceTest extends ServiceTestCase<GenerateFrameService> {

  private static final int WAIT_TIME = 10;

  public GenerateFrameServiceTest() {
    super(GenerateFrameService.class);
  }

  public void testFrameGeneration() throws Exception {
    deleteFile(
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME));
    File mAppDirectory =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            AppConstants.DFG_DIR_NAME);

    // Pick a random device
    Device mDevice = getRandomDevice();
    // Make the test screenshot
    Uri screenshotUri = makeTestScreenShot(mDevice);

    Intent intent = new Intent(getSystemContext(), GenerateFrameService.class);
    intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, mDevice);
    intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, screenshotUri);
    startService(intent);
    assertThat(getService()).isNotNull();

    Thread.sleep(WAIT_TIME * 1000);

    assertThat(mAppDirectory).exists().isDirectory();
    String mGeneratedFilePath = getGeneratedImagePath(mAppDirectory);
    // The file Path is relative to the app directory, make it absolute
    mGeneratedFilePath = mAppDirectory + File.separator + mGeneratedFilePath;
    File generatedImage = new File(mGeneratedFilePath);
    assertThat(generatedImage).exists().isFile();

    // Clean up
    ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
        AbstractGenerateFrameService.DFG_NOTIFICATION_ID);
    deleteFile(new File(getPath(screenshotUri)));
    deleteFile(generatedImage);
    deleteFile(mAppDirectory);
  }

  /**
   * Delete a file.
   * If it is a folder, delete all files recursively.
   *
   * @param file File or folder to delete.
   */
  private void deleteFile(File file) {
    Ln.d("Deleting : " + file.getAbsolutePath());
    if (file.isDirectory()) {
      //directory is empty, then delete it
      if (file.list().length == 0) {
        file.delete();
      } else {
        //list all the directory contents
        String files[] = file.list();
        for (String temp : files) {
          //construct the file structure
          File fileDelete = new File(file, temp);
          //recursive delete
          deleteFile(fileDelete);
        }

        //check the directory again, if empty then delete it
        if (file.list().length == 0) {
          file.delete();
        }
      }
    } else {
      //if file, then delete it
      file.delete();
    }
  }

  /**
   * Get the generated image path.
   * Looks through mAppDirectory and returns the first image.
   *
   * @return The first image in the directory.
   */
  private String getGeneratedImagePath(File directory) {
    String files[] = directory.list();
    if (files.length == 0) {
      return null;
    } else {
      return files[0];
    }
  }

  /** Get a random device. */
  private Device getRandomDevice() {
    int random = new Random().nextInt(DeviceProvider.getDevices().size());
    Device device = DeviceProvider.getDevices().get(random);
    return device;
  }

  public String getPath(Uri uri) {
    Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
    cursor.moveToFirst();
    String imageFilePath = cursor.getString(0);
    cursor.close();
    return imageFilePath;
  }

  /** Make a screenshot matching this device's dimension. */
  private Uri makeTestScreenShot(Device device) throws IOException {
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    directory.mkdirs();
    File screenshot =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "test.png");

    ContentValues values = new ContentValues();
    ContentResolver resolver = getSystemContext().getContentResolver();
    values.put(MediaStore.Images.ImageColumns.DATA, screenshot.getAbsolutePath());
    values.put(MediaStore.Images.ImageColumns.TITLE, "test");
    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "test");
    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Bitmap bmp;
    if (new Random().nextBoolean()) {
      bmp = Bitmap.createBitmap(device.getPortSize()[1], device.getPortSize()[0], conf);
    } else {
      bmp = Bitmap.createBitmap(device.getPortSize()[0], device.getPortSize()[1], conf);
    }
    Canvas canvas = new Canvas(bmp);
    canvas.drawARGB(255, 237, 28, 36);
    OutputStream os = new FileOutputStream(screenshot);
    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
    os.flush();
    os.close();
    bmp.recycle();

    // update file size in the database
    values.clear();
    values.put(MediaStore.Images.ImageColumns.SIZE, screenshot.length());
    resolver.update(imageUri, values, null, null);

    return imageUri;
  }
}
