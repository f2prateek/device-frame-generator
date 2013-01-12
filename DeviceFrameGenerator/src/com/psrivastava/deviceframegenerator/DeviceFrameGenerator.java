
package com.psrivastava.deviceframegenerator;

import static com.psrivastava.deviceframegenerator.util.LogUtils.makeLogTag;
import static com.psrivastava.deviceframegenerator.util.StorageUtils.STORAGE_DIRECTORY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import com.psrivastava.deviceframegenerator.devices.GalaxyNexus;
import com.psrivastava.deviceframegenerator.devices.HTCOneX;
import com.psrivastava.deviceframegenerator.devices.Nexus10;
import com.psrivastava.deviceframegenerator.devices.Nexus4;
import com.psrivastava.deviceframegenerator.devices.Nexus7;
import com.psrivastava.deviceframegenerator.devices.NexusS;
import com.psrivastava.deviceframegenerator.devices.SamsungGalaxyNote;
import com.psrivastava.deviceframegenerator.devices.SamsungGalaxyS3;
import com.psrivastava.deviceframegenerator.devices.SamsungGalaxyTab2;
import com.psrivastava.deviceframegenerator.devices.Xoom;
import com.psrivastava.deviceframegenerator.util.UnmatchedDimensionsException;

public class DeviceFrameGenerator {

    private static final String LOGTAG = makeLogTag(DeviceFrameGenerator.class);

    /**
     * frames the given screenshot using the parameters
     * 
     * @param context
     * @param device : the device for framing
     * @param orientation : orientation of screenshot
     * @param screenshot : screenshot to be framed
     * @param withShadow : If should be generated with shadow
     * @param withGlare : if should be generated with glare
     * @return the path to the saved image
     * @throws IOException couldn't convert image to mutable
     * @throws UnmatchedDimensionsException couldn't match screenshot to the
     *             device
     */
    public static String combine(Context context, Device device, Bitmap screenshot,
            Boolean withShadow, Boolean withGlare) throws IOException, UnmatchedDimensionsException {

        String orientation = checkDimensions(device, screenshot);

        int[] offset = orientation.compareTo("port") == 0 ? device.getPortOffset() : device
                .getLandOffset();

        String shadowString = device.getId() + "_" + orientation + "_shadow";
        String backString = device.getId() + "_" + orientation + "_back";
        String foreString = device.getId() + "_" + orientation + "_fore";

        File folder = new File(STORAGE_DIRECTORY);
        folder.mkdirs();

        Bitmap shadow = convertToMutable(BitmapFactory.decodeResource(
                context.getResources(),
                context.getResources().getIdentifier(shadowString, "drawable",
                        context.getPackageName())));
        Bitmap back = convertToMutable(BitmapFactory.decodeResource(context.getResources(), context
                .getResources().getIdentifier(backString, "drawable", context.getPackageName())));
        Bitmap fore = convertToMutable(BitmapFactory.decodeResource(context.getResources(), context
                .getResources().getIdentifier(foreString, "drawable", context.getPackageName())));

        Canvas comboImage;

        if (withShadow) {
            comboImage = new Canvas(shadow);
            comboImage.drawBitmap(back, 0f, 0f, null);
        } else {
            comboImage = new Canvas(back);
        }

        comboImage.drawBitmap(back, 0f, 0f, null);
        screenshot.createScaledBitmap(screenshot, device.getPortSize()[0], device.getPortSize()[1],
                false);
        comboImage.drawBitmap(screenshot, offset[0], offset[1], null);

        if (withGlare) {
            comboImage.drawBitmap(fore, 0f, 0f, null);
        }

        // To write the file out to the SDCard:
        OutputStream os = null;
        File f = new File(STORAGE_DIRECTORY);
        File[] files = f.listFiles();

        Calendar c = Calendar.getInstance();
        String fileName = device.getId() + "_" + c.get(Calendar.YEAR) + "-"
                + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-"
                + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + files.length
                + ".png";

        os = new FileOutputStream(STORAGE_DIRECTORY + fileName);
        if (withShadow) {
            shadow.compress(CompressFormat.PNG, 50, os);
        } else {
            back.compress(CompressFormat.PNG, 50, os);
        }

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, STORAGE_DIRECTORY + fileName);
        values.put(MediaColumns.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaColumns.MIME_TYPE, "image/png");

        shadow.recycle();
        fore.recycle();
        back.recycle();

        return STORAGE_DIRECTORY + fileName;

    }

    /**
     * checks if screenshot matches size of device
     * 
     * @param device
     * @param screenshot
     * @return port if matched to portrait and land if matched to landscape
     * @throws UnmatchedDimensionsException if could not match to the device
     */
    public static String checkDimensions(Device device, Bitmap screenshot)
            throws UnmatchedDimensionsException {

        float aspect1 = (float)screenshot.getHeight() / (float)screenshot.getWidth();
        float aspect2 = (float)device.getPortSize()[1] / (float)device.getPortSize()[0];

        Log.e(LOGTAG,
                "Screenshot height is " + screenshot.getHeight() + " " + device.getPortSize()[1]
                        + " and width is " + screenshot.getWidth() + " " + device.getPortSize()[0]
                        + " aspect1 = " + aspect1 + " aspect2 = " + aspect2);

        if (aspect1 == aspect2) {
            return "port";
        } else if (aspect1 == 1 / aspect2) {
            return "land";
        }

        throw new UnmatchedDimensionsException();

    }

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't
     * allocates more memory that there is already allocated. Required for
     * API<14
     * 
     * @param imgIn - Source image. It will be released, and should not be used
     *            more
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
        Config type = imgIn.getConfig();

        // Copy the byte to the file
        // Assume source bitmap loaded using options.inPreferredConfig =
        // Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
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

    public static ArrayList<Device> getAllDevices() {
        ArrayList<Device> deviceList = new ArrayList<Device>();
        deviceList.add(new NexusS());
        deviceList.add(new GalaxyNexus());
        deviceList.add(new HTCOneX());
        deviceList.add(new SamsungGalaxyS3());
        deviceList.add(new Nexus4());
        deviceList.add(new SamsungGalaxyNote());
        deviceList.add(new SamsungGalaxyTab2());
        deviceList.add(new Nexus7());
        deviceList.add(new Xoom());
        deviceList.add(new Nexus10());
        return deviceList;
    }

}
