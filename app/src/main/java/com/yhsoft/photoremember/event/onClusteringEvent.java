package com.yhsoft.photoremember.event;

import com.google.android.gms.maps.model.LatLngBounds;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.database.model.PhotoMarker;

import java.util.ArrayList;

public class onClusteringEvent {
    LatLngBounds bounds;  //
    ArrayList<Photo> offsetItem;
    ArrayList<PhotoMarker> offsetMarkerItem;

    public onClusteringEvent(LatLngBounds bounds, ArrayList<Photo> offsetItem, ArrayList<PhotoMarker> offsetMarkerItem) {
        this.bounds = bounds;
        this.offsetItem = offsetItem;
        this.offsetMarkerItem = offsetMarkerItem;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public ArrayList<Photo> getPhotos() {

        return offsetItem;
    }

    public ArrayList<PhotoMarker> getPhotoMarkers() {

        return offsetMarkerItem;
    }

}
