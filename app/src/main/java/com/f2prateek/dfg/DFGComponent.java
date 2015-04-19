package com.f2prateek.dfg;

import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.ui.activities.MainActivity;
import com.f2prateek.dfg.ui.activities.ReceiverActivity;
import com.f2prateek.dfg.ui.fragments.AboutFragment;
import com.f2prateek.dfg.ui.fragments.DeviceFragment;

public interface DFGComponent {
  void inject(DFGApplication app);

  void inject(MainActivity activity);

  void inject(ReceiverActivity activity);

  void inject(DeviceFragment fragment);

  void inject(AboutFragment fragment);

  void inject(GenerateFrameService service);

  void inject(GenerateMultipleFramesService service);
}
