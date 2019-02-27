package com.yhsoft.photoremember.event;

/**
 * Created by design on 2015-05-28.
 */
public class Action_UpEvent {
    long millis_left ;
    long millis_right;

    public Action_UpEvent(long millis_left, long millis_right ){
        this.millis_left = millis_left;
        this.millis_right = millis_right;
    }

    public long getLeftMillis() {
        return millis_left;
    }

    public long getRightMillis() {
        return millis_right;
    }

}
