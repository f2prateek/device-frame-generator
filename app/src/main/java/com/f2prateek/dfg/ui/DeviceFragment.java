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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.BitmapUtils;
import com.squareup.otto.Bus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import javax.inject.Inject;
import java.lang.ref.WeakReference;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class DeviceFragment extends SherlockFragment implements View.OnClickListener {

    private static final String LOGTAG = makeLogTag(DeviceFragment.class);
    private static final int RESULT_SELECT_PICTURE = 542;

    @InjectView(R.id.tv_device_resolution)
    TextView tv_device_resolution;
    @InjectView(R.id.tv_device_size)
    TextView tv_device_size;
    @InjectView(R.id.tv_device_name)
    TextView tv_device_name;
    @InjectView(R.id.ib_device_thumbnail)
    ImageButton ib_device_thumbnail;

    private Device mDevice;
    private static LruCache<String, Bitmap> mMemoryCache;
    private int mNum;

    public static DeviceFragment newInstance(int num) {
        DeviceFragment f = new DeviceFragment();
        buildImageCache();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        f.setRetainInstance(true);
        return f;
    }

    private static void buildImageCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return BitmapUtils.getByteCount(bitmap) / 1024;
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num", 0) : 0;
        mDevice = DeviceProvider.getDevices().get(mNum);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);
        Views.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadBitmap(mDevice.getThumbnail(), ib_device_thumbnail);
        tv_device_size.setText(mDevice.getPhysicalSize() + "\" @ " + mDevice.getDensity() + "dpi");
        tv_device_name.setText(mDevice.getName());
        tv_device_name.setOnClickListener(this);
        tv_device_resolution.setText(mDevice.getRealSize()[0] + "x" + mDevice.getRealSize()[1]);
        ib_device_thumbnail.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_default_device:
                saveDeviceAsDefault();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeviceAsDefault() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, mNum);
        editor.commit();
        String text = getSherlockActivity().getResources().getString(R.string.saved_as_default_message, mDevice.getName());
        Crouton.makeText(getSherlockActivity(), text, Style.CONFIRM).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_device_thumbnail:
                getScreenshotImageFromUser();
                break;
            case R.id.tv_device_name:
                openDevicePage();
                break;
        }
    }

    private void getScreenshotImageFromUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)),
                RESULT_SELECT_PICTURE);
    }

    private void openDevicePage() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(mDevice.getUrl()));
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Intent intent = new Intent(getSherlockActivity(), GenerateFrameService.class);
            intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, mDevice);
            intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, selectedImageUri);
            getSherlockActivity().startService(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(resId, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(getResources(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

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
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            final Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(
                    getResources(), params[0], 400, 400);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}