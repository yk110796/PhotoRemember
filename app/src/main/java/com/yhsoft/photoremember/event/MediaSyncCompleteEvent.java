package com.yhsoft.photoremember.event;

public class MediaSyncCompleteEvent {
    private boolean mResult;

    public MediaSyncCompleteEvent(boolean res) {
        mResult = res;
    }

    public boolean getResult() {
        return mResult;
    }
}
