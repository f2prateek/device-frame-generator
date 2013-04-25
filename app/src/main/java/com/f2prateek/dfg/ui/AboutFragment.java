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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.bugsense.trace.BugSenseHandler;
import com.f2prateek.dfg.R;
import com.inscription.ChangeLogDialog;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.about_list)
    ListView mListView;

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
        List<TwoLineListItem> list = new ArrayList<TwoLineListItem>();
        list.add(new TwoLineListItem(R.string.developer, R.string.prateek_srivastava));
        list.add(new TwoLineListItem(R.string.designer, R.string.taylor_ling));
        list.add(new TwoLineListItem(R.string.version, R.string.current_version_number));
        list.add(new TwoLineListItem(R.string.changelog, R.string.changelog_summary));
        mListView.setAdapter(new TwoLineListAdapter(getActivity(), list));
        mListView.setOnItemClickListener(this);
        getDialog().setTitle(R.string.about);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                openUrl("http://about.f2prateek.com");
                break;
            case 1:
                openUrl("http://androiduiux.com");
                break;
            case 2:
                break;
            case 3:
                final ChangeLogDialog changeLogDialog = new ChangeLogDialog(getActivity());
                changeLogDialog.show();
                break;
        }
    }

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    class TwoLineListItem {
        int title;
        int summary;

        TwoLineListItem(int title, int summary) {
            this.title = title;
            this.summary = summary;
        }
    }

    class TwoLineListAdapter extends BaseAdapter {

        final List<TwoLineListItem> mItems;
        final Context mContext;

        TwoLineListAdapter(Context context, List<TwoLineListItem> items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public TwoLineListItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(android.R.layout.two_line_list_item, parent, false);
            }

            mContext.getResources();
            TwoLineListItem item = getItem(position);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(mContext.getResources().getString(item.title));
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(mContext.getResources().getString(item.summary));

            return convertView;
        }
    }

}
