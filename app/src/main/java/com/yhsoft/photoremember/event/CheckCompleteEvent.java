package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.ui.CheckItem;

import java.util.ArrayList;

public class CheckCompleteEvent {

    private int mFilterType = 0;
    private ArrayList<CheckItem> mList = new ArrayList<>();
    private String targetTag;

    public CheckCompleteEvent(ArrayList<CheckItem> list, int type, String target) {
        mList = list;
        mFilterType = type;
        targetTag = target;
    }

    public ArrayList<CheckItem> getList() {
        return mList;
    }

    public int getType() {
        return mFilterType;
    }

    public String getTargetTag() {
        return targetTag;
    }
}
