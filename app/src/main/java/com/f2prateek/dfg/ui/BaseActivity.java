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

import android.content.Intent;
import android.os.Bundle;
import butterknife.Views;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.DFGApplication;

public class BaseActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DFGApplication.getInstance().inject(this);
        BugSenseHandler.initAndStartSession(this, AppConstants.BUG_SENSE_API_KEY);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        Views.inject(this);
    }

    @Override
    public void onDestroy() {
        BugSenseHandler.closeSession(BaseActivity.this);
        super.onDestroy();
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

}
