/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.ui;

import android.graphics.Bitmap;

/**
 * Created by James on 1/19/15.
 */
public class CommentItem {
    private int mFriendID;
    private Bitmap mPhotoURL;
    private String mName;   // For Test
    private String dateString;
    private String mComments;

    public CommentItem(int id, Bitmap url, String name, String date, String comment) {
        mFriendID = id;
        mPhotoURL = url;
        mName = name;
        dateString = date;
        mComments = comment;
    }

    public int getID() {
        return mFriendID;
    }

    public Bitmap getPhotoURL() {
        return mPhotoURL;
    }

    public String getName() {
        return mName;
    }

    public String getTime() {
        return dateString;
    }

    public String getComment() {
        return mComments;
    }
}
