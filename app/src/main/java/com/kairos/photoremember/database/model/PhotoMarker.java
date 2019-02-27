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

public class PhotoMarker implements ClusterItem {
    public final int photoId;
    private final LatLng mPosition;

    public  PhotoMarker(LatLng position, int id) {
        photoId = id;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }




}
