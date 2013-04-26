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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.bugsense.trace.BugSenseHandler;
import com.f2prateek.dfg.R;
import com.inscription.ChangeLogDialog;

public class AboutFragment extends DialogFragment implements View.OnClickListener {

    @InjectView(R.id.iv_app_logo)
    ImageView mAppLogoImage;
    @InjectView(R.id.tv_version_number)
    TextView mVersionText;
    @InjectView(R.id.tv_gplus)
    TextView mGPlusText;
    @InjectView(R.id.tv_twitter)
    TextView mTwitterText;
    @InjectView(R.id.tv_designer)
    TextView mDesignerText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
        BugSenseHandler.sendEvent("About screen!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        Views.inject(this, v);
        getDialog().setTitle(R.string.about);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppLogoImage.setOnClickListener(this);
        mVersionText.setOnClickListener(this);
        mGPlusText.setOnClickListener(this);
        mTwitterText.setOnClickListener(this);
        mDesignerText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_app_logo:
                openUrl("f2prateek.com/Device-Frame-Generator");
                break;
            case R.id.tv_version_number:
                final ChangeLogDialog changeLogDialog = new ChangeLogDialog(getActivity());
                changeLogDialog.show();
                break;
            case R.id.tv_gplus:
                openUrl("https://prof iles.google.com/f2prateek");
                break;
            case R.id.tv_twitter:
                openUrl("https://twitter.com/f2prateek");
                break;
            case R.id.tv_designer:
                openUrl("http://androiduiux.com/");
                break;
        }
    }

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
