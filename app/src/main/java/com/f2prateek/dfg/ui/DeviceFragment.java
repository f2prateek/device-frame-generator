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

package com.f2prateek.dfg.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.model.Device;
import com.f2prateek.dfg.model.DeviceProvider;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

public class DeviceFragment extends BaseFragment {

  private static final int RESULT_SELECT_PICTURE = 542;
  @Inject SharedPreferences sharedPreferences;
  @InjectView(R.id.tv_device_resolution) TextView tv_device_resolution;
  @InjectView(R.id.tv_device_size) TextView tv_device_size;
  @InjectView(R.id.tv_device_name) TextView tv_device_name;
  @InjectView(R.id.iv_device_thumbnail) ImageView iv_device_thumbnail;
  @InjectView(R.id.iv_device_default) ImageView iv_device_default;
  private Device device;
  private int deviceNum;

  public static DeviceFragment newInstance(int num) {
    DeviceFragment f = new DeviceFragment();
    Bundle args = new Bundle();
    args.putInt("num", num);
    f.setArguments(args);
    f.setRetainInstance(true);
    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    deviceNum = getArguments() != null ? getArguments().getInt("num", 0) : 0;
    device = DeviceProvider.getDevices().get(deviceNum);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_device, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Picasso.with(getActivity()).load(device.getThumbnail()).into(iv_device_thumbnail);
    iv_device_default.bringToFront();
    iv_device_default.setImageResource(
        isDefault() ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
    tv_device_size.setText(device.getPhysicalSize() + "\" @ " + device.getDensity());
    tv_device_name.setText(device.getName());
    tv_device_resolution.setText(device.getRealSize()[0] + "x" + device.getRealSize()[1]);
  }

  @OnClick(R.id.iv_device_default)
  public void updateDefaultDevice() {
    if (isDefault()) {
      return;
    }
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, deviceNum);
    editor.commit();
    bus.post(new Events.DefaultDeviceUpdated(deviceNum));
  }

  @Subscribe
  public void onDefaultDeviceUpdated(Events.DefaultDeviceUpdated event) {
    iv_device_default.post(new Runnable() {
      @Override public void run() {
        iv_device_default.setImageResource(
            isDefault() ? R.drawable.ic_action_star_selected : R.drawable.ic_action_star);
      }
    });
  }

  private boolean isDefault() {
    return deviceNum == sharedPreferences.getInt(AppConstants.KEY_PREF_DEFAULT_DEVICE, 0);
  }

  @OnClick(R.id.iv_device_thumbnail)
  public void getScreenshotImageFromUser() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)),
        RESULT_SELECT_PICTURE);
  }

  @OnClick(R.id.tv_device_name)
  public void openDevicePage() {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(device.getUrl()));
    startActivity(i);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
      Uri selectedImageUri = data.getData();
      Intent intent = new Intent(getActivity(), GenerateFrameService.class);
      intent.putExtra(AppConstants.KEY_EXTRA_DEVICE, device);
      intent.putExtra(AppConstants.KEY_EXTRA_SCREENSHOT, selectedImageUri);
      getActivity().startService(intent);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
}