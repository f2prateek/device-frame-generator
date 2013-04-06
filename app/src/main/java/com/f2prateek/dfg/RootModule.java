package com.f2prateek.dfg;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module
        (
                includes = {
                        AndroidModule.class,
                        DFGModule.class
                }
        )
public class RootModule {
}