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

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.util.BindingListAdapter;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageListAdapter extends BindingListAdapter<Uri> {

  public ImageListAdapter(Context context, List<Uri> list) {
    super(context, list);
  }

  @Override public void bindView(Uri item, View v) {
    Picasso.with(getContext()).load(item).into((ImageView) v);
    v.setTag(item);
  }

  @Override public View newView(LayoutInflater inflater, int type, ViewGroup parent) {
    ImageView imageView = (ImageView) inflater.inflate(R.layout.grid_item_image, parent, false);
    return imageView;
  }
}