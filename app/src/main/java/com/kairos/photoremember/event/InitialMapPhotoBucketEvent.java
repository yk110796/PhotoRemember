/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.event;

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
