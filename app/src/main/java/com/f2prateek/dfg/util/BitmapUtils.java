/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BitmapUtils {

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't
     * allocates more memory that there is already allocated. Required for
     * API<14
     *
     * @param imgIn - Source image. It will be released, and should not be used
     *              more
     * @return a copy of imgIn, but mutable.
     * @throws IOException
     */
    public static Bitmap convertToMutable(Bitmap imgIn) throws IOException {
        // this is the file going to use temporally to save the bytes.
        // This file will not be a image, it will store the raw image data.
        File file = new File(Environment.getExternalStorageDirectory() + File.separator
                + "temp.tmp");

        // Open an RandomAccessFile
        // Make sure you have added uses-permission
        // android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        // into AndroidManifest.xml file
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        // get the width and height of the source bitmap.
        int width = imgIn.getWidth();
        int height = imgIn.getHeight();
        Bitmap.Config type = imgIn.getConfig();

        // Copy the byte to the file
        // Assume source bitmap loaded using options.inPreferredConfig =
        // Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
        imgIn.copyPixelsToBuffer(map);
        // recycle the source bitmap, this will be no longer used.
        imgIn.recycle();
        System.gc();// try to force the bytes from the imgIn to be released

        // Create a new bitmap to load the bitmap again. Probably the memory
        // will be available.
        imgIn = Bitmap.createBitmap(width, height, type);
        map.position(0);
        // load it back from temporary
        imgIn.copyPixelsFromBuffer(map);
        // close the temporary file and channel , then delete that also
        channel.close();
        randomAccessFile.close();

        // delete the temp file
        file.delete();

        return imgIn;
    }

    /**
     * Returns the number of bytes used to store this bitmap's pixels.
     * Support version, checks SDK version to switch between custom version,
     * and API provided version.
     *
     * @param bitmap
     * @return bytes used to store this bitmap's pixels
     */
    public static int getByteCount(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        } else {
            return bitmap.getByteCount();
        }
    }

}
