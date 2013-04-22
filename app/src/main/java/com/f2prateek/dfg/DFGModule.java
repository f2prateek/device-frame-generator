/*
 * Copyright 2013 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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