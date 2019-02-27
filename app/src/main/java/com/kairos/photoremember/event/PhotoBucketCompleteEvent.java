/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.event;

import com.kairos.photoremember.ui.PhotoBucketItem;

public class PhotoBucketCompleteEvent {

    private PhotoBucketItem mItem;

    public PhotoBucketCompleteEvent(PhotoBucketItem item) {
        mItem = item;
    }

    public PhotoBucketItem getItem() {
        return mItem;
    }
}
