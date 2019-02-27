package com.yhsoft.photoremember.event;

import java.util.ArrayList;

public class PhotoDetailEvent {
    private int ID;
    private ArrayList<Integer> list = new ArrayList<>();

    public PhotoDetailEvent(int id, ArrayList<Integer> item) {
        ID = id;
        list = item;
    }

    public int getID() {
        return ID;
    }

    public ArrayList<Integer> getPhotoList() {
        return list;
    }
}
