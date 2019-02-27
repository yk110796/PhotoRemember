package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.database.model.Place;

public class PlaceSelectEvent {
    private Place mPlace;

    public PlaceSelectEvent(Place item) {
        mPlace = item;
    }

    public Place getPlace() {
        return mPlace;
    }
}
