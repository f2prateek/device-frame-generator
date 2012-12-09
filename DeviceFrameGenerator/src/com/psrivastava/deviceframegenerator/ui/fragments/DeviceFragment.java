
package com.psrivastava.deviceframegenerator.ui.fragments;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.DeviceFrameGenerator;
import com.psrivastava.deviceframegenerator.R;
import com.psrivastava.deviceframegenerator.otto.BusProvider;
import com.psrivastava.deviceframegenerator.otto.DeviceClickedEvent;
import com.psrivastava.deviceframegenerator.otto.DeviceDefaultChangedEvent;
import com.psrivastava.deviceframegenerator.util.LogUtils;

public class DeviceFragment extends SherlockFragment {
    private static final String LOGTAG = LogUtils.makeLogTag(DeviceFragment.class);

    Context mContext;

    Device mDevice;

    int mNum;

    private static LruCache<String, Bitmap> mMemoryCache;

    ToggleButton default_toggle;

    /**
     * Container Activity must implement this interface. Starts frame generation
     * with this device as the target
     */
    public interface OnDeviceClickedListener {
        public void onDeviceClicked(int deviceNumber);
    }

    /**
     * Container Activity must implement this interface. Starts frame generation
     * with this device as the target
     */
    public interface OnDefaultDeviceChangedListener {
        public void onDefaultDeviceChanged(int deviceNumber);
    }

    public static DeviceFragment newInstance(int num) {
        DeviceFragment f = new DeviceFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = (Context)activity;

    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mDevice = DeviceFrameGenerator.getAllDevices().get(mNum);

        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number
                // of items.
                // 12+ return bitmap.getByteCount();
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.device_fragment, container, false);

        TextView tv = ((TextView)v.findViewById(R.id.tv_device_title));
        tv.setText(mDevice.getTitle());
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mDevice.getUrl()));
                startActivity(i);
                mContext.startActivity(i);
            }
        });

        ((TextView)v.findViewById(R.id.tv_device_size)).setText(mDevice.getPhysicalSize() + "\" @ "
                + mDevice.getDensity() + "dpi");
        ((TextView)v.findViewById(R.id.tv_device_resolution)).setText(mDevice.getPortSize()[0]
                + "x" + mDevice.getPortSize()[1]);

        ImageButton thumbnail = (ImageButton)v.findViewById(R.id.iv_device_thumbnail);
        loadBitmap(mDevice.getThumbnail(), thumbnail);
        thumbnail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new DeviceClickedEvent(mNum));
            }

        });

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isDefault = mNum == sPrefs.getInt(AppConstants.KEY_DEVICE_POSITION, 0);
        default_toggle = (ToggleButton)v.findViewById(R.id.tb_default_device);
        default_toggle.setChecked(isDefault);
        default_toggle.bringToFront();

        default_toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (!isChecked) {
                    // don't allow clicking already checkd buttons
                    button.setChecked(true);
                } else {
                    BusProvider.getInstance().post(new DeviceDefaultChangedEvent(mNum));
                }
            }

        });

        return v;
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
            return (BitmapWorkerTask)bitmapWorkerTaskReference.get();
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
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageButton imageButton) {
        if (imageButton != null) {
            final Drawable drawable = imageButton.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference imageButtonReference;

        private int data = 0;

        public BitmapWorkerTask(ImageButton imageButton) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            imageButtonReference = new WeakReference(imageButton);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];

            // TODO: change height and width to dyanmic values of the imageview
            // - better for different sizes
            if (isAdded()) {
                final Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), data);
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                return bitmap;
            }

            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageButtonReference != null && bitmap != null) {
                final ImageButton imageButton = (ImageButton)imageButtonReference.get();
                if (imageButton != null) {
                    imageButton.setImageBitmap(bitmap);
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
