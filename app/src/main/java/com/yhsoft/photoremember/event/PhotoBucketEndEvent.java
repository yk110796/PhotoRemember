package com.yhsoft.photoremember.event;

import com.yhsoft.photoremember.ui.PhotoBucketItem;

import java.util.ArrayList;

/*
//     모든 사진을 PhotoBucketItem list 단위로
 //    list에 삽입후 전달하는 event
 */
public class PhotoBucketEndEvent {
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

    public PhotoBucketEndEvent(ArrayList<PhotoBucketItem> list) {
        this.list = list;
    }

    public ArrayList<PhotoBucketItem> getList() {
        return list;
    }

}
