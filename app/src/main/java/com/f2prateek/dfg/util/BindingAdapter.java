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

package com.f2prateek.dfg.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An implementation of {@link android.widget.BaseAdapter} which uses the new/bind pattern for its
 * views.
 */
public abstract class BindingAdapter extends BaseAdapter {

  private final Context context;
  private final LayoutInflater inflater;

  public BindingAdapter(Context context) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
  }

  public Context getContext() {
    return context;
  }

  @Override public final View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);
    if (convertView == null) {
      convertView = newView(inflater, type, parent);
    }
    bindView(position, type, convertView);
    return convertView;
  }

  /** Create a new instance of a view for the specified {@code type}. */
  public abstract View newView(LayoutInflater inflater, int type, ViewGroup parent);

  /** Bind the data for the specified {@code position} to the {@code view}. */
  public abstract void bindView(int position, int type, View view);
}
