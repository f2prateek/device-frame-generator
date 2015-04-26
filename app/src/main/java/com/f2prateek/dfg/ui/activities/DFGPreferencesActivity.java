package com.f2prateek.dfg.ui.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.InjectView;
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
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import javax.inject.Inject;

public class DFGPreferencesActivity extends BaseActivity {

  @Inject Analytics analytics;

  @Inject @ShadowEnabled BooleanPreference shadowEnabledPreference;
  @Inject @GlareEnabled BooleanPreference glareEnabledPreference;
  @Inject @BlurBackgroundEnabled BooleanPreference blurBackgroundEnabledPreference;
  @Inject @ColorBackgroundEnabled BooleanPreference colorBackgroundEnabledPreference;
  @Inject @BackgroundColor EnumPreference<BackgroundColor.Option> backgroundColorOptionPreference;
  @Inject @CustomBackgroundColor IntPreference customBackgroundColorPreference;
  @Inject @BackgroundPaddingPercentage IntPreference backgroundPaddingPercentagePreference;
  @Inject @BackgroundBlurRadius IntPreference backgroundBlurRadiusPreference;

  @InjectView(R.id.shadow_preference) Switch shadowPreferenceSwitch;
  @InjectView(R.id.glare_preference) Switch glarePreferenceSwitch;
  @InjectView(R.id.blur_background_preference) Switch blurBackgroundPreferenceSwitch;
  @InjectView(R.id.color_background_preference) Switch colorBackgroundPreferenceSwitch;
  @InjectView(R.id.background_color_preference) Spinner backgroundColorPreferenceSpinner;

  /** Flag to suppress preference listeners from being invoked during initialization. */
  boolean initializing;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    analytics.screen(null, "Preferences");

    inflateView(R.layout.activity_preferences);

    initializing = true;
    initBooleanPreferenceSwitch(shadowPreferenceSwitch, shadowEnabledPreference);
    initBooleanPreferenceSwitch(glarePreferenceSwitch, glareEnabledPreference);
    initBooleanPreferenceSwitch(blurBackgroundPreferenceSwitch, blurBackgroundEnabledPreference);
    initBooleanPreferenceSwitch(colorBackgroundPreferenceSwitch, colorBackgroundEnabledPreference);
    initBackgroundColorAdapter();
    initializing = false;
  }

  private void initBooleanPreferenceSwitch(Switch preferenceSwitch, BooleanPreference preference) {
    preferenceSwitch.setChecked(preference.get());
  }

  private void initBackgroundColorAdapter() {
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
            } else {
              Ln.d("Ignoring re-selection of background color %s", selected);
            }
          }

          @Override public void onNothingSelected(AdapterView<?> adapterView) {
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

    boolean newValue = blurBackgroundPreferenceSwitch.isChecked();

    updateBooleanPreference(blurBackgroundPreferenceSwitch, blurBackgroundEnabledPreference,
        "Blur Background", "blur_background_enabled", R.string.blur_background_enabled,
        R.string.blur_background_disabled);

    if (newValue && colorBackgroundEnabledPreference.get()) {
      colorBackgroundPreferenceSwitch.toggle();
    }
  }

  @OnCheckedChanged(R.id.color_background_preference) void onColorBackgroundPreferenceChanged() {
    if (initializing) return;

    boolean newValue = colorBackgroundPreferenceSwitch.isChecked();

    updateBooleanPreference(colorBackgroundPreferenceSwitch, colorBackgroundEnabledPreference,
        "Color Background", "color_background_enabled", R.string.color_background_enabled,
        R.string.color_background_disabled);

    if (newValue && blurBackgroundEnabledPreference.get()) {
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

    if (newValue) {
      Crouton.makeText(this, enabledMessage, Style.CONFIRM).show();
    } else {
      Crouton.makeText(this, disabledMessage, Style.ALERT).show();
    }

    Ln.d("%s preference updated to %s", eventName, newValue);
  }
}
