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

import com.google.android.gms.maps.model.LatLng;
import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.event.ErrorEvent;
import com.kairos.photoremember.event.PhotoBucketEndEvent;
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
public class PhotoBucketTask extends AsyncTask<Void, PhotoBucketItem, Boolean> {

    private PhotoBucketItem resultItem;
    private long start, end;
    private int mBucketRange;
    private double top, bottom, left, right;
    Context mContext;
    ArrayList<Photo> photo = null;
    ArrayList<PhotoBucketItem> allPhotoBucket = new ArrayList<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DebugLog.e("onPreExecute()");

        /**
         * We will rebuild the photo buckets using below information
         */
        start = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP);
        end = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM);
        mBucketRange = Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE);

        //map range
        //*******
        mContext = PhoTrace.getContext();
        PhoTrace app = (PhoTrace)mContext.getApplicationContext();
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
        if (photo.size() > 0) {
            // Main loop for all photos
            do {
                current = photo.get(position);
                if (current.getDate() > start || current.getDate() < end) {
                    position++;
                    DebugLog.e("Skip");
                    continue;
                }

                int[] dateValue = DateUtil.getDisplayDate(current.getDate());

                ArrayList<Integer> list = new ArrayList<>();
                ArrayList<LatLng> latlng = new ArrayList<>();
                int addIndex = position; //add시킬때
                boolean isExit = false;

                do {
                    Photo tempPhoto = photo.get(addIndex);

                    int[] tempDate = DateUtil.getDisplayDate(tempPhoto.getDate());
                     //맵에 사진이 있는지 확인
                    if (isIncluded(dateValue, tempDate)) {
                            list.add(tempPhoto.getID());
                            latlng.add(new LatLng(tempPhoto.getLatitude(), tempPhoto.getLongitude())); //lat lng save
                            addIndex++;
                   } else {
                        isExit = true;
                   }
                } while (addIndex < photo.size() && !isExit);
                DebugLog.e("list size : " + list.size());
                    resultItem = new PhotoBucketItem();
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
                    // Lazy loading at the PhotoBucketFragment
                    resultItem.setRegion(latlng);
                    resultItem.setCount(String.valueOf(list.size()));
                    resultItem.setPhotoItem(list);

                    allPhotoBucket.add(resultItem);
                    position = addIndex;
            } while(position < photo.size());
            DebugLog.e("doinback finish");
            return true;
        } else {
            // No Photo
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
        }else{
            //photobucketfragment로 allbucket 전달
            BusProvider.getBus().post(new PhotoBucketEndEvent(allPhotoBucket));
            ArrayList<Integer> allBucket = new ArrayList<>();
            for (int i = 0; i < allPhotoBucket.size(); i++) {
                if (allPhotoBucket.get(i).getPhotoItem().size() > 0) {
                    allBucket.addAll(allPhotoBucket.get(i).getPhotoItem());
                }
            }
            ClusteringTask task = new ClusteringTask(allBucket);
            task.execute();
        }
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

    private boolean isMapIncluded(double lat, double lng) {
        if(right > 0) {
            if ((top >= lat && right >= lng) && (bottom <= lat && left <= lng)) {
                return true;
            } else {
                BusProvider.getBus().post(new ErrorEvent("Invalid status"));
            }
        }else{
            if ((top >= lat && right <= lng) && (bottom <= lat && left <= lng)) {
                return true;
            } else {
                BusProvider.getBus().post(new ErrorEvent("Invalid status"));
            }
        }
        return false;
    }


}
