/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.event;

import android.support.v4.app.Fragment;

public class FragmentSwitchEvent {

    private final Fragment fragment;
    private int switchMode;
    private String backTag;

    public FragmentSwitchEvent(Fragment frag, int mode) {
        this.fragment = frag;
        this.switchMode = mode;
    }

    public final Fragment getFragment() {
        return fragment;
    }

    public int getReplace() {
        return switchMode;
    }

}
