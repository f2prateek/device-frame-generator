package com.f2prateek.dfg.ui;

import static com.f2prateek.dfg.core.Constants.Extra.NEWS_ITEM;
import android.os.Bundle;
import android.widget.TextView;

import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.News;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class NewsActivity extends BootstrapActivity {

    @InjectExtra(NEWS_ITEM) protected News newsItem;

    @InjectView(R.id.tv_title) protected TextView title;
    @InjectView(R.id.tv_content) protected TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(newsItem.getTitle());

        title.setText(newsItem.getTitle());
        content.setText(newsItem.getContent());

    }

}
