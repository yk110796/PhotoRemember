package com.yhsoft.photoremember.ui;

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
