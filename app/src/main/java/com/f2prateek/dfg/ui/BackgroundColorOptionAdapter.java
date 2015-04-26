package com.f2prateek.dfg.ui;

import android.content.Context;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.prefs.BackgroundColor;

public class BackgroundColorOptionAdapter extends EnumAdapter<BackgroundColor.Option> {
  public BackgroundColorOptionAdapter(Context context) {
    super(context, BackgroundColor.Option.class);
  }

  @Override protected String getName(BackgroundColor.Option item) {
    switch (item) {
      case CUSTOM:
        return getContext().getString(R.string.custom_color);
      case VIBRANT:
        return getContext().getString(R.string.vibrant_color);
      case VIBRANT_DARK:
        return getContext().getString(R.string.vibrant_dark_color);
      case VIBRANT_LIGHT:
        return getContext().getString(R.string.vibrant_light_color);
      case MUTED:
        return getContext().getString(R.string.muted_color);
      case MUTED_DARK:
        return getContext().getString(R.string.muted_dark_color);
      case MUTED_LIGHT:
        return getContext().getString(R.string.muted_light_color);
      default:
        throw new IllegalArgumentException("unknown color: " + item);
    }
  }
}
