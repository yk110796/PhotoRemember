/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.database.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Photo implements ClusterItem {
    public static final String ID = "id";
//    public static final String HASH = "hash";
//    public static final String MEMBER_ID = "member_id";
    public static final String DATE = "date";
    public static final String LAT = "latitude";
    public static final String LNG = "longitude";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
//    public static final String EXPOSE_LEVEL = "expose_level";
    public static final String PLACE = "place_id";

    private int id;
//    private int hash;
//    private int member_id;
    private long date;
    private double latitude;
    private double longitude;
    private String title;
    private String description;
//    private int expose_level;
    private int place_id;

    private LatLng mLatOne; // for clustering
    //public final int photoId; //for clustering

    public Photo() {    }


    // for clustering

    public Photo(LatLng latLng) {
        mLatOne  = latLng;
    }

    public Photo(int id, LatLng latLng) {
        // = id;
        mLatOne  = latLng;
    }
    public void setID(int id) {
        this.id = id;
    }
//    public void setHash(int hash) {
//        this.hash = hash;
//    }

//    public void setOwner(int id) {
//        member_id = id;
//    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setLatitude(double lat) {
        latitude = lat;
    }

    public void setLongitude(double lng) {
        longitude = lng;
    }

    public void setTitle(String str) {
        title = str;
    }

    public void setDescription(String str) {
        description = str;
    }

//    public void setExpose_level(int expose) {
//        expose_level = expose;
//    }

    public void setPlace(int id) {
        place_id = id;
    }

    public int getID() {
        return id;
    }

//    public int getHash() {
//        return hash;
//    }

//    public int getOwner() {
//        return member_id;
//    }

    public long getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

//    public int getExpose_level() {
//        return expose_level;
//    }

    public int getPlace() {
        return place_id;
    }




    @Override
    public LatLng getPosition() {
        return mLatOne;
    }


}
