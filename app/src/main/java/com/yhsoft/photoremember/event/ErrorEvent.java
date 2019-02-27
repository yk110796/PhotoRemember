package com.yhsoft.photoremember.event;

public class ErrorEvent {
    public CharSequence mErrorMsg;

    public ErrorEvent(CharSequence msg) {
        mErrorMsg = msg;
    }

    public CharSequence getMessage() {
        return mErrorMsg;
    }
}
