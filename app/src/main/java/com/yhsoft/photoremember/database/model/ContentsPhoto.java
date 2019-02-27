package com.yhsoft.photoremember.database.model;

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
     *
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
