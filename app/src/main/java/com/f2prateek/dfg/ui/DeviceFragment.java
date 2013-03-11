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

package com.f2prateek.dfg.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import java.lang.ref.WeakReference;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class DeviceFragment extends RoboSherlockFragment {

    private static final String LOGTAG = makeLogTag(DeviceFragment.class);

    private static LruCache<String, Bitmap> mMemoryCache;

    @Inject
    Bus bus;
    Device mDevice;
    int mNum;
    TextView tv_device_resolution;
    TextView tv_device_size;
    TextView tv_device_name;
    ImageButton ib_device_thumbnail;

    public static DeviceFragment newInstance(int num) {
        DeviceFragment f = new DeviceFragment();
        buildCache();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    private static void buildCache() {
        if (mMemoryCache == null) {
            Log.d(LOGTAG, "creating new memory cache");
            // Get max available VM memory, exceeding this amount will throw an
            // OutOfMemory exception. Stored in kilobytes as LruCache takes an
            // int in its constructor.
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    final int byteCount;

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                        byteCount = bitmap.getRowBytes() * bitmap.getHeight();
                    } else {
                        byteCount = bitmap.getByteCount();
                    }

                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return byteCount / 1024;
                }
            };
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mDevice = DeviceProvider.getDevices().get(mNum);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_device, container, false);
        tv_device_size = (TextView) v.findViewById(R.id.tv_device_size);
        tv_device_size.setText(mDevice.getPhysicalSize() + "\" @ " + mDevice.getDensity() + "dpi");
        tv_device_name = (TextView) v.findViewById(R.id.tv_device_name);
        tv_device_name.setText(mDevice.getName());
        tv_device_resolution = (TextView) v.findViewById(R.id.tv_device_resolution);
        tv_device_resolution.setText(mDevice.getPortSize()[0] + "x" + mDevice.getPortSize()[1]);
        ib_device_thumbnail = (ImageButton) v.findViewById(R.id.ib_device_thumbnail);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadBitmap(mDevice.getThumbnail(), ib_device_thumbnail);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return (BitmapWorkerTask) bitmapWorkerTaskReference.get();
        }
    }

    public void loadBitmap(int resId, ImageButton imageButton) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageButton.setImageBitmap(bitmap);
            return;
        }

        if (cancelPotentialWork(resId, imageButton)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageButton);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), null, task);
            imageButton.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    public static boolean cancelPotentialWork(int data, ImageButton imageButton) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageButton);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageButton, or an existing task was
        // cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageButton imageButton) {
        if (imageButton != null) {
            final Drawable drawable = imageButton.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference imageButtonReference;

        private int data = 0;

        public BitmapWorkerTask(ImageButton imageButton) {
            // Use a WeakReference to ensure the ImageButton can be garbage
            // collected
            imageButtonReference = new WeakReference(imageButton);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];

            // TODO: change height and width to dyanmic values of the imageButton
            // - better for different sizes
            if (isAdded()) {
                final Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), data);
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                return bitmap;
            }

            return null;
        }

        // Once complete, see if ImageButton is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageButtonReference != null && bitmap != null) {
                final ImageButton imageButton = (ImageButton) imageButtonReference.get();
                if (imageButton != null) {
                    imageButton.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


}