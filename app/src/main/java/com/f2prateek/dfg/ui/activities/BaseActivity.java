/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.f2prateek.dart.Dart;
import com.f2prateek.dfg.DFGApplication;
import com.f2prateek.dfg.ui.AppContainer;
import com.squareup.otto.Bus;
import javax.inject.Inject;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

  @Inject Bus bus;
  @Inject AppContainer appContainer;

  private ViewGroup container;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DFGApplication app = DFGApplication.get(this);
    app.inject(this);

    Dart.inject(this);

    container = appContainer.get(this, app);
  }

  protected void inflateView(int layoutId) {
    getLayoutInflater().inflate(layoutId, container);
    injectViews();
  }

  private void injectViews() {
    ButterKnife.inject(this);
  }

  @Override protected void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override protected void onPause() {
    bus.unregister(this);
    super.onPause();
  }
}
