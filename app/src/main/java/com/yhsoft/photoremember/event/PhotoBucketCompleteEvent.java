package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.ui.PhotoBucketItem;

public class PhotoBucketCompleteEvent {

    private PhotoBucketItem mItem;

    public PhotoBucketCompleteEvent(PhotoBucketItem item) {
        mItem = item;
    }

    public PhotoBucketItem getItem() {
        return mItem;
    }
}
