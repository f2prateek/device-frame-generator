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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.InjectView;
import com.f2prateek.dfg.R;
import java.util.ArrayList;

public class PictureViewerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

  @InjectView(R.id.listView) ListView listView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_picture_viewer);

    Intent intent = getIntent();
    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    listView.setAdapter(new ImageListAdapter(this, imageUris));
    listView.setOnItemClickListener(this);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Uri uri = (Uri) view.getTag();
    Intent viewIntent = new Intent(Intent.ACTION_VIEW);
    viewIntent.setDataAndType(uri, "image/png");
    viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(viewIntent);
  }
}