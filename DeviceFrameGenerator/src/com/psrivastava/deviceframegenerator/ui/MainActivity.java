
package com.psrivastava.deviceframegenerator.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawer.OnDrawerStateChangeListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.psrivastava.deviceframegenerator.AppConstants;
import com.psrivastava.deviceframegenerator.Device;
import com.psrivastava.deviceframegenerator.DeviceFrameGenerator;
import com.psrivastava.deviceframegenerator.R;
import com.psrivastava.deviceframegenerator.otto.BusProvider;
import com.psrivastava.deviceframegenerator.otto.DeviceClickedEvent;
import com.psrivastava.deviceframegenerator.otto.DeviceDefaultChangedEvent;
import com.psrivastava.deviceframegenerator.ui.fragments.DeviceFragment;
import com.psrivastava.deviceframegenerator.util.LogUtils;
import com.psrivastava.deviceframegenerator.util.StorageUtils;
import com.psrivastava.deviceframegenerator.util.UnmatchedDimensionsException;
import com.squareup.otto.Subscribe;

import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

public class MainActivity extends SherlockFragmentActivity {

    private static final String LOGTAG = LogUtils.makeLogTag(MainActivity.class);

    private static final int RESULT_SELECT_PICTURE = 21;

    private MenuDrawer mMenuDrawer;

    private MenuAdapter mMenuAdapter;

    private MenuListView mMenuList;

    DevicePagerAdapter mAdapter;

    ViewPager mPager;

    SharedPreferences mPrefs;

    // TODO: bad?
    static Activity mActivity;

    static ArrayList<Device> mDeviceList;

    int mClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // This has to be called before setContentView and you must use the
        // class in com.actionbarsherlock.view and NOT android.view
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_CONTENT);
        mMenuDrawer.setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Object> items = new ArrayList<Object>();
        items.add(new Item("Help", R.drawable.ic_action_help));
        items.add(new Item("Bug Tracker", R.drawable.ic_action_warning));
        items.add(new Category("Developed By Prateek Srivastava"));
        items.add(new Item("Email", R.drawable.ic_action_mail));
        items.add(new Item("Twitter", R.drawable.ic_action_twitter));
        items.add(new Item("Donate", R.drawable.ic_action_paypal));
        items.add(new Item("Source", R.drawable.ic_action_folder_open));

        // A custom ListView is needed so the drawer can be notified when it's
        // scrolled. This is to update the position
        // of the arrow indicator.
        mMenuList = new MenuListView(this);
        mMenuAdapter = new MenuAdapter(items);
        mMenuList.setAdapter(mMenuAdapter);
        mMenuList.setOnItemClickListener(mMenuItemClickListener);
        mMenuList.setOnScrollChangedListener(new MenuListView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mMenuDrawer.invalidate();
            }
        });

        mMenuDrawer.setMenuView(mMenuList);
        mMenuDrawer.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                switch (newState) {
                    case MenuDrawer.STATE_CLOSED:
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        break;
                    case MenuDrawer.STATE_OPEN:
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        break;
                }
            }
        });

        // TODO:For pre-jellybean devices, spinner is shown onCreate, temporary
        // fix while I investigate more
        setSupportProgressBarIndeterminateVisibility(false);

        mDeviceList = DeviceFrameGenerator.getAllDevices();

        mAdapter = new DevicePagerAdapter(getSupportFragmentManager());
        mActivity = this;

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                mMenuDrawer.setTouchMode(position == 0 ? MenuDrawer.TOUCH_MODE_FULLSCREEN
                        : MenuDrawer.TOUCH_MODE_NONE);
            }
        });

        mPager.setAdapter(mAdapter);

        if (mPrefs == null) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        }

        mPager.setCurrentItem(mPrefs.getInt(AppConstants.KEY_DEVICE_POSITION, 0));

        mMenuDrawer.setTouchMode(mPager.getCurrentItem() == 0 ? MenuDrawer.TOUCH_MODE_FULLSCREEN
                : MenuDrawer.TOUCH_MODE_NONE);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    // Handle single image being sent
                    handleReceivedImage(intent);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    // Handle multiple images being sent
                    handleReceivedMultipleImages(intent);
                }
            }
        }

    }

    /**
     * handles processing images when only one is sent
     * 
     * @param intent
     */
    void handleReceivedImage(Intent intent) {

        Uri imageUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String selectedImagePath = StorageUtils.getPath(this, imageUri);
            new FrameGeneratorTask(mDeviceList.get(mPrefs.getInt(AppConstants.KEY_DEVICE_POSITION,
                    0))).execute(selectedImagePath);
        }
    }

    /**
     * handles processing multiple images
     * 
     * @param intent
     */
    void handleReceivedMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        ArrayList<String> imagePaths = new ArrayList<String>();

        for (int i = 0; i < imageUris.size(); i++) {
            imagePaths.add(StorageUtils.getPath(this, imageUris.get(i)));
        }

        new MultipleFrameGeneratorTask(this, mDeviceList.get(mPrefs.getInt(
                AppConstants.KEY_DEVICE_POSITION, 0))).execute(imagePaths);

    }

    public static class DevicePagerAdapter extends FragmentStatePagerAdapter {
        public DevicePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mDeviceList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return DeviceFragment.newInstance(position);
        }

    }

    @Subscribe
    public void onDeviceClicked(DeviceClickedEvent event) {
        int deviceNumber = event.number;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                RESULT_SELECT_PICTURE);

        mClicked = deviceNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = StorageUtils.getPath(this, selectedImageUri);
                new FrameGeneratorTask(mDeviceList.get(mClicked)).execute(selectedImagePath);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onDefaultDeviceChanged(DeviceDefaultChangedEvent event) {
        int deviceNumber = event.number;
        Editor editor = mPrefs.edit();
        editor.putInt(AppConstants.KEY_DEVICE_POSITION, deviceNumber);
        editor.commit();

        mAdapter = new DevicePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(deviceNumber);

        Crouton.cancelAllCroutons();
        Crouton.makeText(
                this,
                getResources().getString(R.string.device_saved,
                        mDeviceList.get(deviceNumber).getTitle()), Style.CONFIRM).show();

    }

    private class FrameGeneratorTask extends AsyncTask<String, Integer, String> {

        Device device;

        FrameGeneratorTask(Device device) {
            this.device = device;
        }

        @Override
        protected void onPreExecute() {
            setSupportProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            String path = drawImage(device, params[0]);
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            setSupportProgressBarIndeterminateVisibility(false);

            if (result != null) {
                galleryAddPic(result);
                viewImage(result);
            }

            super.onPostExecute(result);
        }
    }

    private class MultipleFrameGeneratorTask extends AsyncTask<ArrayList<String>, Integer, String> {

        Activity activity;

        Device device;

        int successCount = 0;

        int maxCount = 0;

        MultipleFrameGeneratorTask(Activity activity, Device device) {
            this.activity = activity;
            this.device = device;
        }

        @Override
        protected void onPreExecute() {
            setSupportProgress(0);
            setSupportProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            if (params.length != 1) {
                return null;
            }

            ArrayList<String> imagePaths = params[0];
            maxCount = imagePaths.size();

            for (int i = 0; i < imagePaths.size(); i++) {
                String result = drawImage(device, imagePaths.get(i));

                if (result != null) {
                    Log.v(LOGTAG, "the path for the new image is " + result);
                    galleryAddPic(result);
                    successCount++;
                    publishProgress((Window.PROGRESS_END - Window.PROGRESS_START)
                            / imagePaths.size() * (i + 1));
                } else {
                    Log.e(LOGTAG, "result was null");
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress.length != 1) {
                return;
            }
            setSupportProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            setSupportProgressBarIndeterminateVisibility(false);
            setSupportProgress(0);

            if (successCount != maxCount) {

                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Crouton.makeText(
                                activity,
                                String.format(
                                        ((Context)activity).getResources().getString(
                                                R.string.images_not_generated), maxCount
                                                - successCount), Style.CONFIRM).show();

                    }

                });

            }

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Crouton.makeText(
                            mActivity,
                            String.format(
                                    ((Context)mActivity).getResources().getString(
                                            R.string.images_generated), successCount),
                            Style.CONFIRM).show();

                }

            });

            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.openMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * draws images and saves them to SD card using the Util class
     * 
     * @param selectedImagePath
     * @return path to saved file
     */
    private String drawImage(Device device, String selectedImagePath) {

        Bitmap screenshot = null;

        try {
            screenshot = DeviceFrameGenerator.convertToMutable(BitmapFactory
                    .decodeFile(selectedImagePath));
        } catch (IOException e) {
            e.printStackTrace();
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Crouton.makeText(mActivity, R.string.error_unable_to_save, Style.ALERT).show();

                }

            });
            return null;
        }

        String pathToFile = null;

        try {
            pathToFile = DeviceFrameGenerator.combine(this, device, screenshot,
                    mPrefs.getBoolean(AppConstants.KEY_WITH_SHADOW, true),
                    mPrefs.getBoolean(AppConstants.KEY_WITH_GLARE, true));
        } catch (IOException e) {
            e.printStackTrace();
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Crouton.makeText(mActivity, R.string.error_unable_to_save, Style.ALERT).show();

                }

            });
            return null;
        } catch (UnmatchedDimensionsException e) {
            e.printStackTrace();
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Crouton.makeText(mActivity, R.string.error_dimensions_not_matched, Style.ALERT)
                            .show();

                }

            });
            return null;
        }

        screenshot.recycle();

        return pathToFile;
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static class CheckBoxActionProvider extends ActionProvider {

        Context mContext;

        SharedPreferences sPrefs;

        public CheckBoxActionProvider(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public View onCreateActionView() {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.actionbar_checkbox_layout, null);

            sPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            CheckBox glare = (CheckBox)view.findViewById(R.id.check_glare);
            glare.setChecked(sPrefs.getBoolean(AppConstants.KEY_WITH_GLARE, true));
            glare.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Editor edit = sPrefs.edit();
                    edit.putBoolean(AppConstants.KEY_WITH_GLARE, isChecked);
                    edit.commit();
                    Crouton.cancelAllCroutons();
                    if (isChecked) {
                        Crouton.makeText(mActivity, R.string.glare_enabled, Style.CONFIRM).show();
                    } else {
                        Crouton.makeText(mActivity, R.string.glare_disabled, Style.ALERT).show();
                    }
                }

            });

            CheckBox shadow = (CheckBox)view.findViewById(R.id.check_shadow);
            shadow.setChecked(sPrefs.getBoolean(AppConstants.KEY_WITH_SHADOW, true));
            shadow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Editor edit = sPrefs.edit();
                    edit.putBoolean(AppConstants.KEY_WITH_SHADOW, isChecked);
                    edit.commit();
                    Crouton.cancelAllCroutons();
                    if (isChecked) {
                        Crouton.makeText(mActivity, R.string.shadow_enabled, Style.CONFIRM).show();
                    } else {
                        Crouton.makeText(mActivity, R.string.shadow_disabled, Style.ALERT).show();
                    }
                }

            });

            return view;
        }
    }

    private static final class Item {

        String mTitle;

        int mIconRes;

        Item(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }

    private static final class Category {

        String mTitle;

        Category(String title) {
            mTitle = title;
        }
    }

    private class MenuAdapter extends BaseAdapter {

        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Item ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position) instanceof Item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Object item = getItem(position);

            if (item instanceof Category) {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_category, parent, false);
                }

                ((TextView)v).setText(((Category)item).mTitle);

            } else {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_item, parent, false);
                }

                TextView tv = (TextView)v;
                tv.setText(((Item)item).mTitle);
                tv.setCompoundDrawablesWithIntrinsicBounds(((Item)item).mIconRes, 0, 0, 0);
            }

            v.setTag(R.id.mdActiveViewPosition, position);

            return v;
        }
    }

    private AdapterView.OnItemClickListener mMenuItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    openUrl(AppConstants.URL_PROJECT_PAGE);
                    break;
                case 1:
                    openUrl(AppConstants.URL_ISSUES);
                    break;
                case 3:
                    email();
                    break;
                case 4:
                    openUrl(AppConstants.URL_TWITTER);
                    break;
                case 5:
                    openUrl(AppConstants.URL_DONATE);
                    break;
                case 6:
                    openUrl(AppConstants.URL_SOURCE);
                    break;
            }
            mMenuDrawer.closeMenu();
        }
    };

    private void email() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
            "f2prateek@gmail.com"
        });
        emailIntent.setType("plain/text");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void openUrl(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * View the image with given path
     * 
     * @param pathToFile
     */
    private void viewImage(String pathToFile) {
        if (pathToFile == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + pathToFile), "image/*");
        startActivity(intent);
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
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }

}
