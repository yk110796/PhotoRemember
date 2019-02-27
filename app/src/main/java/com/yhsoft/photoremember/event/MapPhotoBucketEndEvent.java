package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.ui.PhotoBucketItem;

import java.util.ArrayList;

/*
//     모든 사진을 PhotoBucketItem list 단위로
 //    list에 삽입후 전달하는 event
 */
public class MapPhotoBucketEndEvent {
    /*
        private PhotoBucketItem mItem;

        public PhotoBucketEndEvent(PhotoBucketItem item) {
            mItem = item;
        }
       public PhotoBucketItem getItem() {
            return mItem;
        }
     */
    private ArrayList<PhotoBucketItem> list;
    private long mMaxTime;
    private long mMinTime;

    public MapPhotoBucketEndEvent(ArrayList<PhotoBucketItem> list, long mMaxTime, long mMinTime) {
        this.list = list;
        this.mMaxTime = mMaxTime;
        this.mMinTime = mMinTime;
    }

    public ArrayList<PhotoBucketItem> getList() {
        return list;
    }

    public long getMaxTime() {
        return mMaxTime;
    }

    public long getMinTime() {
        return mMinTime;
    }

}
