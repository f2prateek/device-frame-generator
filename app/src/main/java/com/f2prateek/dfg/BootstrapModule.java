package com.f2prateek.dfg;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;

/**
 * Module for setting up custom bindings in RoboGuice.
 */
public class BootstrapModule extends AbstractModule {

    @Override
    protected void configure() {

        // We want Otto to be bound as a singleton as one instance only needs
        // to be present in this app
        bind(Bus.class).in(Singleton.class);

    }

}
