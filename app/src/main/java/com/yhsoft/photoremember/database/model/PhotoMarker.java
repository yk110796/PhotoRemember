package com.yhsoft.photoremember.database.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PhotoMarker implements ClusterItem {
    public final int photoId;
    private final LatLng mPosition;

    public PhotoMarker(LatLng position, int id) {
        photoId = id;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


}
