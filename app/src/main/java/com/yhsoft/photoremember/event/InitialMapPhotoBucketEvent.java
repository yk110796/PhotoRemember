package com.yhsoft.photoremember.event;

import java.util.ArrayList;

public class InitialMapPhotoBucketEvent {
    ArrayList<Integer> list;
    private long mMaxTime;
    private long mMinTime;

    public InitialMapPhotoBucketEvent(ArrayList<Integer> list, long mMaxTime, long mMinTime) {
        this.list = list;
        this.mMaxTime = mMaxTime;
        this.mMinTime = mMinTime;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public long getMaxTime() {
        return mMaxTime;
    }

    public long getMinTime() {
        return mMinTime;
    }
}
