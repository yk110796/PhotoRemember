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
