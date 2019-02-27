/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.database.model;

/**
 * Created by James on 1/11/15.
 */
public class ContentsPhoto {
    public static final String CONTENT_ID = "content_id";
    public static final String PHOTO_ID = "photo_id";

    private int contentID;
    private int photoID;

    /**
     * Content Photo join table is always set with each id
     * @param tid
     * @param pid
     */
    public void setIDs(int tid, int pid) {
        contentID = tid;
        photoID = pid;
    }

    public int getPhotoID() {
        return photoID;
    }

    public int getContentID() {
        return contentID;
    }
}
