package com.f2prateek.dfg;

import com.f2prateek.dfg.prefs.DebugPreferencesModule;
import com.f2prateek.dfg.prefs.PreferencesModule;
import com.f2prateek.dfg.ui.DebugUiModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * The core debug component for DFGApplication
 */
@Singleton @Component(modules = {
    DFGApplicationModule.class, DeviceModule.class, DebugUiModule.class, PreferencesModule.class,
    DebugPreferencesModule.class
})
public interface RealDFGComponent extends DFGComponent {
  /**
   * An initializer that creates the graph from an application.
   */
  final static class Initializer {
    static DFGComponent init(DFGApplication app) {
      return Dagger_RealDFGComponent.builder()
          .dFGApplicationModule(new DFGApplicationModule(app))
          .build();
    }

    private Initializer() {
      throw new AssertionError("no instances");
    }
  }
}