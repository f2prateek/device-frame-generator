package com.f2prateek.dfg.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnCheckedChanged;
import com.f2prateek.dfg.R;
import com.f2prateek.dfg.prefs.BackgroundBlurRadius;
import com.f2prateek.dfg.prefs.BackgroundColor;
import com.f2prateek.dfg.prefs.BackgroundPaddingPercentage;
import com.f2prateek.dfg.prefs.BlurBackgroundEnabled;
import com.f2prateek.dfg.prefs.ColorBackgroundEnabled;
import com.f2prateek.dfg.prefs.CustomBackgroundColor;
import com.f2prateek.dfg.prefs.GlareEnabled;
import com.f2prateek.dfg.prefs.ShadowEnabled;
import com.f2prateek.dfg.prefs.model.BooleanPreference;
import com.f2prateek.dfg.prefs.model.EnumPreference;
import com.f2prateek.dfg.prefs.model.IntPreference;
import com.f2prateek.dfg.ui.BackgroundColorOptionAdapter;
import com.f2prateek.ln.Ln;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import java.util.List;
import javax.inject.Inject;

public class UserPreferencesActivity extends BaseActivity {

  @Inject Analytics analytics;

  @Inject @ShadowEnabled BooleanPreference shadowEnabledPreference;
  @Inject @GlareEnabled BooleanPreference glareEnabledPreference;
  @Inject @BlurBackgroundEnabled BooleanPreference blurBackgroundEnabledPreference;
  @Inject @ColorBackgroundEnabled BooleanPreference colorBackgroundEnabledPreference;
  @Inject @BackgroundColor EnumPreference<BackgroundColor.Option> backgroundColorOptionPreference;
  @Inject @CustomBackgroundColor IntPreference customBackgroundColorPreference;
  @Inject @BackgroundPaddingPercentage IntPreference backgroundPaddingPercentagePreference;
  @Inject @BackgroundBlurRadius IntPreference backgroundBlurPreference;

  @InjectView(R.id.shadow_preference) Switch shadowPreferenceSwitch;
  @InjectView(R.id.glare_preference) Switch glarePreferenceSwitch;
  @InjectView(R.id.blur_background_preference) Switch blurBackgroundPreferenceSwitch;
  @InjectView(R.id.color_background_preference) Switch colorBackgroundPreferenceSwitch;
  @InjectView(R.id.background_color_preference) Spinner backgroundColorPreferenceSpinner;
  @InjectView(R.id.custom_background_color_preference) ColorPicker
      customBackgroundColorPreferencePicker;
  @InjectView(R.id.background_padding_percentage_preference) SeekBar
      backgroundPaddingPercentagePreferenceSeekBar;
  @InjectView(R.id.background_blur_preference) SeekBar backgroundBlurPreferenceSeekBar;

  @InjectViews({
      R.id.background_padding_percentage_preference,
      R.id.background_padding_percentage_preference_text
  }) List<View> backgroundPaddingPercentagePreferenceViews;
  @InjectViews({
      R.id.background_blur_preference, R.id.background_blur_preference_text
  }) List<View> backgroundBlurPreferenceViews;
  @InjectViews({
      R.id.background_color_preference, R.id.background_color_preference_text,
      R.id.custom_background_color_preference, R.id.custom_background_color_preference_text
  }) List<View> colorBackgroundPreferenceViews;
  @InjectViews({
      R.id.custom_background_color_preference, R.id.custom_background_color_preference_text
  }) List<View> customColorBackgroundPreferenceViews;

  private static final ButterKnife.Action<View> HIDE = new ButterKnife.Action<View>() {
    @Override public void apply(View view, int i) {
      view.setVisibility(View.GONE);
    }
  };
  private static final ButterKnife.Action<View> SHOW = new ButterKnife.Action<View>() {
    @Override public void apply(View view, int i) {
      view.setVisibility(View.VISIBLE);
    }
  };

  /** Flag to suppress preference listeners from being invoked during initialization. */
  boolean initializing;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    analytics.screen(null, "Preferences");

    inflateView(R.layout.activity_preferences);

    getActionBar().setDisplayHomeAsUpEnabled(true);

    initializing = true;
    initBooleanPreferenceSwitch(shadowPreferenceSwitch, shadowEnabledPreference);
    initBooleanPreferenceSwitch(glarePreferenceSwitch, glareEnabledPreference);
    initBooleanPreferenceSwitch(blurBackgroundPreferenceSwitch, blurBackgroundEnabledPreference);
    initBooleanPreferenceSwitch(colorBackgroundPreferenceSwitch, colorBackgroundEnabledPreference);
    initBackgroundColorOptionSpinner();
    initCustomBackgroundColorPicker();
    // todo: make min and max more reusable - possibly stashed inside IntPreference
    initIntPreferenceSeekBar(backgroundPaddingPercentagePreferenceSeekBar,
        backgroundPaddingPercentagePreference, 0, 100);
    initIntPreferenceSeekBar(backgroundBlurPreferenceSeekBar, backgroundBlurPreference, 0, 25);

    if (blurBackgroundEnabledPreference.get() || colorBackgroundEnabledPreference.get()) {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, SHOW);
    } else {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, HIDE);
    }
    if (blurBackgroundEnabledPreference.get()) {
      ButterKnife.apply(backgroundBlurPreferenceViews, SHOW);
    } else {
      ButterKnife.apply(backgroundBlurPreferenceViews, HIDE);
    }
    if (colorBackgroundEnabledPreference.get()) {
      ButterKnife.apply(colorBackgroundPreferenceViews, SHOW);
      if (backgroundColorOptionPreference.get() == BackgroundColor.Option.CUSTOM) {
        ButterKnife.apply(customColorBackgroundPreferenceViews, SHOW);
      } else {
        ButterKnife.apply(customColorBackgroundPreferenceViews, HIDE);
      }
    } else {
      ButterKnife.apply(colorBackgroundPreferenceViews, HIDE);
    }

    initializing = false;
  }

  private void initIntPreferenceSeekBar(SeekBar seekBar, final IntPreference preference,
      final int min, final int max) {
    int normalizedProgress = preference.get() * (max - min) / 100;
    seekBar.setProgress(normalizedProgress);
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int normalizedValue = progress * (max - min) / 100;
        Ln.d("Updating preference to %s", normalizedValue);
        preference.set(normalizedValue);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        // Ignore
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        // Ignore
      }
    });
  }

  private void initBooleanPreferenceSwitch(Switch preferenceSwitch, BooleanPreference preference) {
    preferenceSwitch.setChecked(preference.get());
  }

  private void initBackgroundColorOptionSpinner() {
    final BackgroundColorOptionAdapter backgroundColorAdapter =
        new BackgroundColorOptionAdapter(this);
    backgroundColorPreferenceSpinner.setAdapter(backgroundColorAdapter);
    final BackgroundColor.Option backgroundColorValue = backgroundColorOptionPreference.get();
    backgroundColorPreferenceSpinner.setSelection(backgroundColorValue.ordinal());
    backgroundColorPreferenceSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            BackgroundColor.Option selected = backgroundColorAdapter.getItem(position);
            if (selected != backgroundColorOptionPreference.get()) {
              Ln.d("Setting background color to %s", selected);
              backgroundColorOptionPreference.set(selected);
              if (selected == BackgroundColor.Option.CUSTOM) {
                ButterKnife.apply(customColorBackgroundPreferenceViews, SHOW);
              } else {
                ButterKnife.apply(customColorBackgroundPreferenceViews, HIDE);
              }
            } else {
              Ln.d("Ignoring re-selection of background color %s", selected);
            }
          }

          @Override public void onNothingSelected(AdapterView<?> adapterView) {
            // Ignore
          }
        });
  }

  private void initCustomBackgroundColorPicker() {
    customBackgroundColorPreferencePicker.setColor(customBackgroundColorPreference.get());
    customBackgroundColorPreferencePicker.setOnColorChangedListener(
        new ColorPicker.OnColorChangedListener() {
          @Override public void onColorChanged(int color) {
            Ln.d("Setting custom background color to %s", color);
            customBackgroundColorPreference.set(color);
          }
        });
  }

  @OnCheckedChanged(R.id.shadow_preference) void onShadowPreferenceChanged() {
    if (initializing) return;

    updateBooleanPreference(shadowPreferenceSwitch, shadowEnabledPreference, "Shadow",
        "shadow_enabled", R.string.shadow_enabled, R.string.shadow_disabled);
  }

  @OnCheckedChanged(R.id.glare_preference) void onGlarePreferenceChanged() {
    if (initializing) return;

    updateBooleanPreference(glarePreferenceSwitch, glareEnabledPreference, "Glare", "glare_enabled",
        R.string.glare_enabled, R.string.glare_disabled);
  }

  @OnCheckedChanged(R.id.blur_background_preference) void onBlurBackgroundPreferenceChanged() {
    if (initializing) return;

    boolean blurBackgroundEnabled = blurBackgroundPreferenceSwitch.isChecked();

    updateBooleanPreference(blurBackgroundPreferenceSwitch, blurBackgroundEnabledPreference,
        "Blur Background", "blur_background_enabled", R.string.blur_background_enabled,
        R.string.blur_background_disabled);

    if (blurBackgroundEnabled) {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, SHOW);
      ButterKnife.apply(backgroundBlurPreferenceViews, SHOW);
    } else {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, HIDE);
      ButterKnife.apply(backgroundBlurPreferenceViews, HIDE);
    }

    if (blurBackgroundEnabled && colorBackgroundEnabledPreference.get()) {
      colorBackgroundPreferenceSwitch.toggle();
    }
  }

  @OnCheckedChanged(R.id.color_background_preference) void onColorBackgroundPreferenceChanged() {
    if (initializing) return;

    boolean colorBackgroundEnabled = colorBackgroundPreferenceSwitch.isChecked();

    updateBooleanPreference(colorBackgroundPreferenceSwitch, colorBackgroundEnabledPreference,
        "Color Background", "color_background_enabled", R.string.color_background_enabled,
        R.string.color_background_disabled);

    if (colorBackgroundEnabled) {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, SHOW);
      ButterKnife.apply(colorBackgroundPreferenceViews, SHOW);
    } else {
      ButterKnife.apply(backgroundPaddingPercentagePreferenceViews, HIDE);
      ButterKnife.apply(colorBackgroundPreferenceViews, HIDE);
    }

    if (colorBackgroundEnabled && blurBackgroundEnabledPreference.get()) {
      blurBackgroundPreferenceSwitch.toggle();
    }
  }

  private void updateBooleanPreference(Switch preferenceSwitch, BooleanPreference booleanPreference,
      String eventName, String trait, @StringRes int enabledMessage,
      @StringRes int disabledMessage) {
    boolean newValue = preferenceSwitch.isChecked();
    booleanPreference.set(newValue);

    analytics.track(eventName + " " + (newValue ? "Enabled" : "Disabled"));
    analytics.identify(new Traits().putValue(trait, newValue));

    Crouton.clearCroutonsForActivity(this);
    if (newValue) {
      Crouton.makeText(this, enabledMessage, Style.CONFIRM).show();
    } else {
      Crouton.makeText(this, disabledMessage, Style.ALERT).show();
    }

    Ln.d("%s preference updated to %s", eventName, newValue);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      Intent homeIntent = new Intent(this, MainActivity.class);
      homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(homeIntent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
