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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class StorageUtils {

    private static final String LOGTAG = makeLogTag(StorageUtils.class);

    /**
     * Checks if storage is available for our use.
     *
     * @return true if storage is available and writeable
     */
    public static boolean checkStorageAvailable() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
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

        Log.i(LOGTAG, mExternalStorageAvailable ? "Storage available" : "Storage Unavailable");
        Log.i(LOGTAG, mExternalStorageWriteable ? "Storage writeable" : "Storage not writeable");

        return (mExternalStorageAvailable && mExternalStorageWriteable);
    }

    /**
     * Gets path of selected image
     *
     * @param uri path of the image
     * @return path of selected image
     */
    public static String getPath(Context context, Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA
        };

        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
