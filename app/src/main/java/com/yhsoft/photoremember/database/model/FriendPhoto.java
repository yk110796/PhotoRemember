package com.yhsoft.photoremember.database.model;

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
