package com.f2prateek.dfg;

import com.f2prateek.dfg.core.AbstractGenerateFrameService;
import com.f2prateek.dfg.core.GenerateFrameService;
import com.f2prateek.dfg.core.GenerateMultipleFramesService;
import com.f2prateek.dfg.ui.*;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
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
                        AboutActivity.class,
                        DeviceFragment.class,
                        AbstractGenerateFrameService.class,
                        GenerateFrameService.class,
                        GenerateMultipleFramesService.class
                }

        )
public class DFGModule {

    @Provides
    @Singleton
    Bus provideOttoBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

}