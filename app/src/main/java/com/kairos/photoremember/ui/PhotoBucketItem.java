/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.ui;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PhotoBucketItem {
    private String mDate;
    private String mCount;
    private int mCountNum;
    private ArrayList<LatLng> mLatLng;
    private ArrayList<Integer> mPhotoItem;
    private ArrayList<Integer> mAllPhotoItem;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getCount() {
        return mCount;
    }

    public int getCountNum() {
        return mCountNum;
    }

    public void setCount(String count) {
        mCount = count;
    }

    public void setCountNum(int countNum) {
        mCountNum = countNum;
    }

    public ArrayList<LatLng> getRegion() {
        return mLatLng;
    }

    public void setRegion(ArrayList<LatLng> latLng) {
        mLatLng = latLng;
    }

    public ArrayList<Integer> getPhotoItem() {
        return mPhotoItem;
    }

    public void setPhotoItem(ArrayList<Integer> item) {
        mPhotoItem = item;
    }

    public ArrayList<Integer> getAllPhotoItem() {
        return mAllPhotoItem;
    }
}

