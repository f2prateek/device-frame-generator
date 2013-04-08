package com.f2prateek.dfg.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.actionbarsherlock.view.ActionProvider;
import com.f2prateek.dfg.AppConstants;
import com.f2prateek.dfg.Events;
import com.f2prateek.dfg.R;
import com.squareup.otto.Bus;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.f2prateek.dfg.util.LogUtils.makeLogTag;

/**
 * Copyright 2013 prateek
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CheckBoxActionProvider extends ActionProvider implements CompoundButton.OnCheckedChangeListener {

    final SharedPreferences sharedPreferences;
    final Context context;
    final Bus BUS;
    private final String LOGTAG = makeLogTag(CheckBoxActionProvider.class);

    public CheckBoxActionProvider(Context context, Bus bus) {
        super(context);
        this.context = context;
        this.BUS = bus;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateActionView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.actionbar_checkbox_layout, null);
        CheckBox glare = (CheckBox) view.findViewById(R.id.check_glare);
        glare.setChecked(sharedPreferences.getBoolean(AppConstants.KEY_PREF_OPTION_GLARE, true));
        glare.setOnCheckedChangeListener(this);
        CheckBox shadow = (CheckBox) view.findViewById(R.id.check_shadow);
        shadow.setChecked(sharedPreferences.getBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, true));
        shadow.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Crouton.cancelAllCroutons();
        switch (buttonView.getId()) {
            case R.id.check_glare:
                editor.putBoolean(AppConstants.KEY_PREF_OPTION_GLARE, isChecked);
                BUS.post(new Events.GlareSettingUpdated(isChecked));
                break;
            case R.id.check_shadow:
                editor.putBoolean(AppConstants.KEY_PREF_OPTION_SHADOW, isChecked);
                BUS.post(new Events.ShadowSettingUpdated(isChecked));
                break;
        }
        editor.commit();
    }
}



