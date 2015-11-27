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

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.f2prateek.ln.DebugLn;

public class CrashlyticsLn extends DebugLn {

  public CrashlyticsLn(String packageName) {
    super(packageName, Log.VERBOSE);
  }

  @Override protected void println(int priority, String msg) {
    Crashlytics.log(msg);
  }

  @Override public void e(Throwable t) {
    super.e(t);
    Crashlytics.logException(t);
  }

  @Override public void e(Throwable throwable, Object s1, Object... args) {
    super.e(throwable, s1, args);
    Crashlytics.logException(throwable);
  }
}