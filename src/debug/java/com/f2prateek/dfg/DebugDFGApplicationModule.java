/*
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.dfg;

import android.content.Context;
import com.f2prateek.dfg.prefs.DebugPreferencesModule;
import com.f2prateek.dfg.ui.DebugUiModule;
import com.segment.analytics.Analytics;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    addsTo = DFGApplicationModule.class,
    includes = {
        DebugUiModule.class, DebugPreferencesModule.class
    },
    overrides = true //
)
public final class DebugDFGApplicationModule {

  @Provides @Singleton @AnalyticsKey String provideAnalyticsKey() {
    return "5UsMbFwbXy";
  }

  @Provides @Singleton //
  Analytics provideAnalytics(@ForApplication Context context, @AnalyticsKey String key) {
    return new Analytics.Builder(context, key).logLevel(Analytics.LogLevel.BASIC).build();
  }
}
