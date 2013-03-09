package com.f2prateek.dfg.ui;

import static com.f2prateek.dfg.core.Constants.Extra.USER;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.f2prateek.dfg.R;
import com.f2prateek.dfg.core.AvatarLoader;
import com.f2prateek.dfg.core.User;
import com.google.inject.Inject;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class UserActivity extends BootstrapActivity {

    @InjectView(R.id.iv_avatar) protected ImageView avatar;
    @InjectView(R.id.tv_name) protected TextView name;

    @InjectExtra(USER) protected User user;

    @Inject protected AvatarLoader avatarLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_view);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatarLoader.bind(avatar, user);
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

    }


}
