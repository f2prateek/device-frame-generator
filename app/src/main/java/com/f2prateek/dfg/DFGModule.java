package com.f2prateek.dfg;

import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.ui.BaseActivity;
import com.f2prateek.dfg.ui.DeviceFragment;
import com.f2prateek.dfg.ui.MainActivity;
import com.f2prateek.dfg.ui.ReceiverActivity;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
        (
                complete = false,

                entryPoints = {
                        DFGApplication.class,
                        BaseActivity.class,
                        MainActivity.class,
                        ReceiverActivity.class,
                        DeviceFragment.class,
                        GenerateFrameService.class,
                        GenerateMultipleFramesService.class
                }

        )
public class DFGModule {

    @Provides
    @Singleton
    Bus provideOttoBus() {
        return new Bus();
    }

}