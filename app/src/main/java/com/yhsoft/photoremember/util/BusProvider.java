package com.yhsoft.photoremember.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import static com.squareup.otto.ThreadEnforcer.ANY;

public final class BusProvider {
    // Generate a event from anywhere
    private static final Bus mBus = new Bus(ANY);

    public static Bus getBus() {
        return mBus;
    }

    private BusProvider() {
    }
}
