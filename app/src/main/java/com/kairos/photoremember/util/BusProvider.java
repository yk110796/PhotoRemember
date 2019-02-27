/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public final class BusProvider {
    // Generate a event from anywhere
    private static final Bus mBus = new Bus(ThreadEnforcer.ANY);

    public static Bus getBus() {
        return mBus;
    }
    private BusProvider() {}
}
