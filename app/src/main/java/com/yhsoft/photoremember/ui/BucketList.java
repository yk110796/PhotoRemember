package com.yhsoft.photoremember.ui;

import java.util.ArrayList;

/**
 * Created by design on 2015-06-02.
 */
public class BucketList {
    private ArrayList<PhotoBucketItem> allBucketList ;
    private int index;

    public BucketList(){
        index = 0;
        allBucketList = new ArrayList<>();
    }
    public BucketList(ArrayList<PhotoBucketItem> list){
        index = 0;
        allBucketList = list;
    }

    public  ArrayList<PhotoBucketItem> getAllBucketList(){
        return allBucketList;
    }

    public void setAllBucketList(ArrayList<PhotoBucketItem>  list){
        allBucketList = list;
    }

    public void add(PhotoBucketItem bucket){
        allBucketList.add(bucket);

      //  allBucketList.add(index, bucket);
    }

}
