package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.database.model.PhotoMarker;

import java.util.ArrayList;

public class onClusteringEventForMap {
    ArrayList<Photo> offsetItem;
    ArrayList<PhotoMarker> offsetMarkerItem;

    public onClusteringEventForMap(ArrayList<Photo> offsetItem, ArrayList<PhotoMarker> offsetMarkerItem) {
        this.offsetItem = offsetItem;
        this.offsetMarkerItem = offsetMarkerItem;
    }


    public ArrayList<Photo> getPhotos() {
        return offsetItem;
    }

    public ArrayList<PhotoMarker> getPhotoMarkers() {

        return offsetMarkerItem;
    }

}
