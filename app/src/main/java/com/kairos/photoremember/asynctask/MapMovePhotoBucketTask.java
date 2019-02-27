/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.event.ErrorEvent;
import com.kairos.photoremember.event.MapPhotoBucketEndEvent;
import com.kairos.photoremember.ui.PhotoBucketItem;
import com.kairos.photoremember.util.BusProvider;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.DebugLog;
import com.kairos.photoremember.util.Preference;

import java.util.ArrayList;

/**
 * PhotoBucket Build Task
 * Precondition : Bucket range must be set - bottom/top
 */
public class MapMovePhotoBucketTask extends AsyncTask<Void, PhotoBucketItem, Boolean> {

    private PhotoBucketItem resultItem;
    private int mBucketRange;
    private double top, bottom, left, right;
    Context mContext;
    ArrayList<Photo> photo = null;
    ArrayList<PhotoBucketItem> allPhotoBucket = new ArrayList<>();
    PhoTrace app;

    long tempMaxDate, tempMinDate;

    int[] dateValue;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DebugLog.e("onPreExecute()");

        /**
         * We will rebuild the photo buckets using below information
         */
        Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_MONTH);
        mBucketRange = Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE);

        //map range
        //*******
        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();
        photo = app.photo;

        top = app.top;
        bottom = app.bottom;
        left = app.left;
        right = app.right;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int position = 0;
        Photo current;
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<LatLng> latlng = new ArrayList<>();
        ArrayList<Long> date = new ArrayList<>();


        int addIndex = 0;

        if (photo.size() > 0) {
            // Main loop for all photos
            do {
                current = photo.get(position);
                //맵에 사진이 있는지 확인
                if (isMapIncluded(current.getLatitude(), current.getLongitude())) {
                    list.add(current.getID());
                    latlng.add(new LatLng(current.getLatitude(), current.getLongitude())); //lat lng save
                    date.add(current.getDate());
                }
                position++;
            } while (position < photo.size());

            //지역에 해당하는 사진들의 최소 최대 날짜 뽑기
            if (list.size() > 0) {
                tempMinDate = date.get(list.size() - 1);
                tempMaxDate = date.get(0);
                Log.d("", "tempMinDate : " + tempMinDate);
                Log.d("", "tempMaxDate : " + tempMaxDate);

                if (tempMaxDate - tempMinDate >= DateUtils.YEAR_IN_MILLIS) {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_YEAR);
                } else if (tempMaxDate - tempMinDate >= (DateUtils.WEEK_IN_MILLIS * 4) && tempMaxDate - tempMinDate < DateUtils.YEAR_IN_MILLIS) {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_MONTH);
                } else {
                    Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_DAY);
                }
            }else{
                tempMinDate = app.leftInMillis;
                tempMaxDate = app.rightInMillis;
            }

            addIndex = 0;

            if (list.size() == 0) {
                dateValue = DateUtil.getDisplayDate(current.getDate());
            } else {
                dateValue = DateUtil.getDisplayDate(date.get(0));
                ArrayList<Integer> lastList = new ArrayList<Integer>(); //리스트 생성
                PhotoBucketItem resultItem = new PhotoBucketItem(); // 버킷 생성
                for(int i = 0; i < list.size() ; i++) {
                    //dateValue = DateUtil.getDisplayDate(date.get(i));
                    int[] tempDate = DateUtil.getDisplayDate(date.get(i));

                    if (!isIncluded(dateValue, tempDate)) {

                        switch (mBucketRange) {
                            case DateUtil.RANGE_YEAR:
                                resultItem.setDate(Integer.toString(dateValue[0]));
                                break;
                            case DateUtil.RANGE_MONTH:
                                resultItem.setDate(dateValue[0] + "." + dateValue[1]);
                                break;
                            case DateUtil.RANGE_DAY:
                                resultItem.setDate(dateValue[0] + "." + dateValue[1] + "." + dateValue[2]);
                                break;
                        }
                        resultItem.setPhotoItem(lastList);
                        resultItem.setCount(String.valueOf(lastList.size()));
                        allPhotoBucket.add(resultItem);
                    }

                    if (!isIncluded(dateValue, tempDate)) {
                        lastList = new ArrayList<Integer>(); //리스트 생성
                        resultItem = new PhotoBucketItem(); // 버킷 생성
                        lastList.add(list.get(i));
                        dateValue = tempDate;

                    } else {
                        lastList.add(list.get(i));
                    }
                }

                switch (mBucketRange) {
                    case DateUtil.RANGE_YEAR:
                        resultItem.setDate(Integer.toString(dateValue[0]));
                        break;
                    case DateUtil.RANGE_MONTH:
                        resultItem.setDate(dateValue[0] + "." + dateValue[1]);
                        break;
                    case DateUtil.RANGE_DAY:
                        resultItem.setDate(dateValue[0] + "." + dateValue[1] + "." + dateValue[2]);
                        break;
                }
                resultItem.setPhotoItem(lastList);
                resultItem.setCount(String.valueOf(lastList.size()));
                allPhotoBucket.add(resultItem);
            }
            DebugLog.e("doinback finish");
            return true;
        } else {
            DebugLog.e("There is no photo");
            return false;
        }

    }


    @Override
    protected void onProgressUpdate(PhotoBucketItem... items) {
        DebugLog.e("Publish PhotoBucket");
    }

    @Override
    protected void onPostExecute(Boolean result) {
        DebugLog.e("onPostExecute");
        if (!result) {
            BusProvider.getBus().post(new ErrorEvent("Failed to get the information from MediaStore"));
        } else {
            BusProvider.getBus().post(new MapPhotoBucketEndEvent(allPhotoBucket, tempMaxDate, tempMinDate));
            ArrayList<Integer> allBucket = new ArrayList<>();
            for (int i = 0; i < allPhotoBucket.size(); i++) {
                if (allPhotoBucket.get(i).getPhotoItem().size() > 0) {
                    allBucket.addAll(allPhotoBucket.get(i).getPhotoItem());
                }
            }
            //ClusteringForMapTask task = new ClusteringForMapTask(allBucket);
            //task.execute();
        }
    }

    private boolean isMapIncluded(double lat, double lng) {
        if ( top > bottom ) { // normal
            if ((top >= lat && bottom <= lat) && (left <= lng && right >= lng)) {
                return true;
            } else {
                BusProvider.getBus().post(new ErrorEvent("Invalid status"));
            }
        } else {
            if ((top <= lat && bottom >= lat) && (left <= lng && right >= lng)) {
                return true;
            } else {
                BusProvider.getBus().post(new ErrorEvent("Invalid status"));
            }
        }
        return false;
    }

    private boolean isIncluded(int[] base, int[] comp) {
        if (mBucketRange == DateUtil.RANGE_DAY) {
            if (base[0] == comp[0] && base[1] == comp[1] && base[2] == comp[2]) {
                return true;
            } else {
                return false;
            }
        } else if (mBucketRange == DateUtil.RANGE_MONTH) {
            if (base[0] == comp[0] && base[1] == comp[1]) {
                return true;
            } else {
                return false;
            }
        } else if (mBucketRange == DateUtil.RANGE_YEAR) {
            if (base[0] == comp[0]) {
                return true;
            } else {
                return false;
            }
        } else {
            BusProvider.getBus().post(new ErrorEvent("Invalid status"));
        }
        return false;
    }

}
