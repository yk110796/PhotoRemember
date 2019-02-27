package com.yhsoft.photoremember.event;

import android.view.View;

public class IgnoreViewEvent {
    private View mView;

    public IgnoreViewEvent(View view) {
        mView = view;
    }

    public View getView() {
        return mView;
    }
}
