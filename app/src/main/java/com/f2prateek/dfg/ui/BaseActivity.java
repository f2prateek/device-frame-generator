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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import butterknife.Views;
import com.f2prateek.dfg.DFGApplication;
import com.squareup.otto.Bus;
import javax.inject.Inject;

@SuppressLint("Registered") public class BaseActivity extends Activity {

  @Inject Bus bus;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((DFGApplication) getApplication()).inject(this);
  }

  @Override
  public void setContentView(int layoutResId) {
    super.setContentView(layoutResId);
    Views.inject(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:  // This is the home button in the top left corner of the screen.
        // Dont call finish! Because activity could have been started by an outside activity and the home button would not operated as expected!
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override
  protected void onPause() {
    bus.unregister(this);
    super.onPause();
  }
}
