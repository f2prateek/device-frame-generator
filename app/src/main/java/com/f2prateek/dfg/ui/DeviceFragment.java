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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.f2prateek.dfg.util.StorageUtils;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.otto.Bus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import roboguice.inject.InjectView;

import javax.inject.Inject;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

public class DeviceFragment extends RoboSherlockFragment implements View.OnClickListener {

    private static final String LOGTAG = makeLogTag(DeviceFragment.class);

    private static final int RESULT_SELECT_PICTURE = 542;

    @Inject
    Bus bus;
    Device mDevice;
    @InjectView(R.id.tv_device_resolution)
    TextView tv_device_resolution;
    @InjectView(R.id.tv_device_size)
    TextView tv_device_size;
    @InjectView(R.id.tv_device_name)
    TextView tv_device_name;
    @InjectView(R.id.ib_device_thumbnail)
    ImageButton ib_device_thumbnail;
    int mNum;

    public static DeviceFragment newInstance(int num) {
        DeviceFragment f = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
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
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String uri = "drawable://" + mDevice.getThumbnail();
        ImageLoader.getInstance().displayImage(uri, ib_device_thumbnail);
        tv_device_size.setText(mDevice.getPhysicalSize() + "\" @ " + mDevice.getDensity() + "dpi");
        tv_device_name.setText(mDevice.getName());
        tv_device_resolution.setText(mDevice.getPortSize()[0] + "x" + mDevice.getPortSize()[1]);
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
        bus.unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_device_thumbnail:
                getScreenshotImageFromUser();
                break;
        }
    }

    private void getScreenshotImageFromUser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                RESULT_SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String screenshotPath = StorageUtils.getPath(getSherlockActivity(), selectedImageUri);
            Intent intent = new Intent(getActivity(), GenerateFrameService.class);
            intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, mDevice);
            intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, screenshotPath);
            getActivity().startService(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}