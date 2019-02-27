package com.yhsoft.photoremember.event;

import java.util.ArrayList;

public class InitialPhotoBucketEvent {
    ArrayList<Integer> list;

    public InitialPhotoBucketEvent(ArrayList<Integer> list) {
        this.list = list;
    }

    public ArrayList<Integer> getList() {
        return list;
    }


}
