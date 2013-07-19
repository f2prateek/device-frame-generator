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

import android.view.View;
import java.util.List;

/** A {@link BindingAdapter} backed by a {@link java.util.List} of {@link T}. */
public abstract class BindingListAdapter<T> extends BindingAdapter {

  protected List<T> list;

  public BindingListAdapter(List<T> list) {
    this.list = list;
  }

  @Override public void bindView(int position, int type, View view) {
    bindView(getItem(position), view);
  }

  public abstract void bindView(T item, View v);

  @Override public int getCount() {
    return list.size();
  }

  @Override public T getItem(int position) {
    return list.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }
}
