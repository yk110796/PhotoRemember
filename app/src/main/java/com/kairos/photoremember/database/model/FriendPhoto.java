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
public class FriendPhoto {
    public static final String FRIEND_ID = "friend_id";
    public static final String PHOTO_ID = "photo_id";

    private int friendID;
    private int photoID;

    public void setIDs(int tid, int pid) {
        friendID = tid;
        photoID = pid;
    }

    public int getPhotoID() {
        return photoID;
    }

    public int getFriendID() {
        return friendID;
    }
}
