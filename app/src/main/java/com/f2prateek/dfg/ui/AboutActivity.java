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
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.f2prateek.dfg.R;
import com.inscription.ChangeLogDialog;
import de.psdev.licensesdialog.LicensesDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.list)
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Resources res = getResources();
        List<TwoLineListItem> list = new ArrayList<TwoLineListItem>();
        list.add(new TwoLineListItem(res.getString(R.string.developer), "Prateek Srivastava"));
        list.add(new TwoLineListItem(res.getString(R.string.designer), "Taylor Ling"));
        list.add(new TwoLineListItem(res.getString(R.string.attribution), res.getString(R.string.attribution_summary)));
        list.add(new TwoLineListItem(res.getString(R.string.changelog), res.getString(R.string.changelog_summary)));

        mListView.setAdapter(new TwoLineListAdapter(list));
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                openUrl("https://twitter.com/f2prateek");
                break;
            case 1:
                openUrl("http://androiduiux.com");
                break;
            case 2:
                final LicensesDialogFragment fragment = LicensesDialogFragment.newInstace(R.raw.attribution);
                fragment.show(getSupportFragmentManager(), null);
                break;
            case 3:
                final ChangeLogDialog changeLogDialog = new ChangeLogDialog(this);
                changeLogDialog.show();
                break;
        }
    }

    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private class TwoLineListAdapter extends BaseAdapter {

        final List<TwoLineListItem> mList;

        private TwoLineListAdapter(List<TwoLineListItem> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public TwoLineListItem getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TwoLineListItem item = getItem(position);

            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(android.R.layout.two_line_list_item, parent, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.title);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(item.summary);
            return convertView;
        }

    }

    class TwoLineListItem {
        String summary;
        String title;

        TwoLineListItem(String title, String summary) {
            this.title = title;
            this.summary = summary;
        }
    }

}
