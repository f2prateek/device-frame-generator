
package com.psrivastava.deviceframegenerator.util;

import static com.psrivastava.deviceframegenerator.util.LogUtils.makeLogTag;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class StorageUtils {

    /**
     * Folder for storage finished images.
     */
    public static final String APP_FOLDER = "/Device-Frame-Generator/";

    /**
     * Storage directory, obtained by combining {@link #APP_FOLDER} and the
     * environment path
     */
    public static final String STORAGE_DIRECTORY = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).toString()
            + APP_FOLDER;

    private static final String LOGTAG = makeLogTag(StorageUtils.class);

    /**
     * @return true if storage is available and writeable
     */
    public static boolean checkStorage() {
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
     * gets path of selected image
     * 
     * @param context
     * @param uri
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
