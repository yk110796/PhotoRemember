/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.event;

import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.database.model.PhotoMarker;

import java.util.ArrayList;

public class onClusteringEventForMap {
    ArrayList<Photo> offsetItem;
    ArrayList<PhotoMarker> offsetMarkerItem;

    public onClusteringEventForMap(ArrayList<Photo> offsetItem,  ArrayList<PhotoMarker> offsetMarkerItem){
        this.offsetItem = offsetItem;
        this.offsetMarkerItem = offsetMarkerItem;
    }


    public ArrayList<Photo> getPhotos(){
        return offsetItem;
    }

    public ArrayList<PhotoMarker> getPhotoMarkers(){

        return offsetMarkerItem;
    }

}
