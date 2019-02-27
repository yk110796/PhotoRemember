package com.yhsoft.photoremember.event;

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
