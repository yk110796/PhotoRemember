/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.asynctask;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.activity.PhoTraceActivity;
import com.kairos.photoremember.database.DataBaseHelper;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.event.ErrorEvent;
import com.kairos.photoremember.event.PhotoBucketCompleteEvent;
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
public class PickModePhotoBucketTask extends AsyncTask<Void, PhotoBucketItem, Boolean> {

    private PhotoBucketItem resultItem;
    private String start, end;
    private int[] bottomValue = new int[3];
    private int[] topValue = new int[3];
    private int mBucketRange;
    private boolean bIsExit = false;
    private double top, bottom, left, right;

    @Override
    protected Boolean doInBackground(Void... voids) {
        int position = 0;
        PhoTraceActivity.failure = false;
        start = Preference.getString(PhoTrace.getContext(), Preference.KEY_PICK_MODE_BUCKET_TOP);
        String[] tempEnd = start.split("\\.");
        if (tempEnd.length == 3) {
            topValue[0] = Integer.parseInt(tempEnd[0]);
            topValue[1] = Integer.parseInt(tempEnd[1]);
            topValue[2] = Integer.parseInt(tempEnd[2]);
        }

        end = Preference.getString(PhoTrace.getContext(), Preference.KEY_PICK_MODE_BUCKET_BOTTOM);
        String[] tempStart = end.split("\\.");
        if (tempStart.length == 3) {
            bottomValue[0] = Integer.parseInt(tempStart[0]);
            bottomValue[1] = Integer.parseInt(tempStart[1]);
            bottomValue[2] = Integer.parseInt(tempStart[2]);
        }

        mBucketRange = Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE);

        //map range
        top = Preference.getLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_TOP);
        bottom = Preference.getLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_BOTTOM);
        left = Preference.getLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_LEFT);
        right = Preference.getLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_RIGHT);


        DataBaseHelper mDB = DataBaseHelper.getInstance();
        //여기 수정할것 app.photo 사용
        ArrayList<Photo> photo = mDB.collectPhotoWithQuery(mDB.SELECT_PHOTO_ALL);

            if (photo.size() > 0) {
                do {
                    /**
                     * Scan nested loop
                     * - Outer loop : change topvalue
                     * - Inner loop : Scan with topvalue
                     */
                    boolean bFindSame = false;
                    do {
                        do {
                            if (IsDateEquals(topValue, bottomValue)) {
                                bIsExit = true;
                            }
                            if (IsDateEquals(topValue, DateUtil.getDisplayDate(photo.get(position).getDate()))) {
                                DebugLog.e("Find same");
                                bFindSame = true;
                                break;
                            } else {
                                DebugLog.e("Skip : " + position);
                                   position++;
                            }
                            if(PhoTraceActivity.failure) {
                                DebugLog.e("Failure1");
                                return false;
                            }
                        } while (position < photo.size() );

                        if (bFindSame) {
                            break;
                        }
                        position = 0;
                        changeHeadValue();
                        if(PhoTraceActivity.failure) {
                            DebugLog.e("Failure2");
                            return false;
                        }
                    } while (!bIsExit );

                    ArrayList<Integer> list = new ArrayList<>();
                    ArrayList<LatLng> latlng = new ArrayList<>();
                    int addIndex = position;
                    boolean bGroupingExit = false;
                    do {
                        Photo tempPhoto = photo.get(addIndex);
                        int[] tempDate = DateUtil.getDisplayDate(tempPhoto.getDate());

                        /**
                         * Grab the photo with top-down sequential search
                         */
                        if (IsDateEquals(topValue, tempDate)) {
                            if (isMapIncluded(tempPhoto.getLatitude(), tempPhoto.getLongitude())) {
                                list.add(tempPhoto.getID());
                                latlng.add(new LatLng(tempPhoto.getLatitude(), tempPhoto.getLongitude()));
                                addIndex++;
                            } else {
                                //isExit = true;
                                addIndex++;
                            }
                        } else {
                            bGroupingExit = true;
                        }
                        if(PhoTraceActivity.failure) {
                            DebugLog.e("Failure3");
                            return false;
                        }
                    } while (addIndex < photo.size() && !bGroupingExit );

                    if (list.size() > 0) {
                        resultItem = new PhotoBucketItem();
                        resultItem.setDate(topValue[0] + "." + topValue[1] + "." + topValue[2]);
                        resultItem.setRegion(latlng);
                        resultItem.setCount(String.valueOf(list.size()));
                        resultItem.setPhotoItem(list);
                        publishProgress(resultItem);
                    }

                    position = addIndex;

                    changeHeadValue();

                } while (position < photo.size() && !bIsExit );
                return true;
            } else {
                DebugLog.e("There is no photo");
                return false;
            }



    }

    @Override
    protected void onProgressUpdate(PhotoBucketItem... items) {
        DebugLog.e("Publish PhotoBucket");
        BusProvider.getBus().post(new PhotoBucketCompleteEvent(items[0]));
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            BusProvider.getBus().post(new ErrorEvent("Failed to get the information from MediaStore"));
        }
    }

    private void changeHeadValue() {
        /**
         * Ugly calculation : decrease date according to the display mode
         */
        if (mBucketRange == DateUtil.RANGE_YEAR) {
            topValue[0]--;
        } else {
            if (topValue[1] == 1) {
                topValue[0]--;
                topValue[1] = 12;
            } else {
                topValue[1]--;
            }
        }
        printDate("After Cal : ", topValue);
    }

    private void printDate(String tag, int[] date) {
        DebugLog.e(tag + " / Date : " + Integer.toString(date[0]) + "." + Integer.toString(date[1]) + "." + Integer.toString(date[2]));
    }

    private boolean IsDateEquals(int[] base, int[] comp) {
        if (base[0] == comp[0] && base[1] == comp[1] && base[2] == comp[2]) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isMapIncluded(double lat, double lng) {
        if ( (top > lat && left < lng) && (bottom < lat && right > lng)) {
            return true;
        } else {
            BusProvider.getBus().post(new ErrorEvent("Invalid status"));
        }
        return false;
    }
}
