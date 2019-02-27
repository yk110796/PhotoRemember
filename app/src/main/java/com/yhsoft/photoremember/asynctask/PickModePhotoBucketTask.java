package com.yhsoft.photoremember.asynctask;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.activity.PhoTraceActivity;
import com.yhsoft.photoremember.database.DataBaseHelper;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.event.ErrorEvent;
import com.yhsoft.photoremember.event.PhotoBucketCompleteEvent;
import com.yhsoft.photoremember.ui.PhotoBucketItem;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.Preference;

import java.util.ArrayList;

import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.activity.PhoTraceActivity.failure;
import static com.yhsoft.photoremember.database.DataBaseHelper.SELECT_PHOTO_ALL;
import static com.yhsoft.photoremember.database.DataBaseHelper.getInstance;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_YEAR;
import static com.yhsoft.photoremember.util.DateUtil.getDisplayDate;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_LEFT;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_RIGHT;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_TOP;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_BUCKET_START_MODE;
import static com.yhsoft.photoremember.util.Preference.KEY_PICK_MODE_BUCKET_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_PICK_MODE_BUCKET_TOP;
import static com.yhsoft.photoremember.util.Preference.getInt;
import static com.yhsoft.photoremember.util.Preference.getLong;
import static com.yhsoft.photoremember.util.Preference.getString;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

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
        failure = false;
        start = getString(getContext(), KEY_PICK_MODE_BUCKET_TOP);
        String[] tempEnd = start.split("\\.");
        if (tempEnd.length == 3) {
            topValue[0] = parseInt(tempEnd[0]);
            topValue[1] = parseInt(tempEnd[1]);
            topValue[2] = parseInt(tempEnd[2]);
        }

        end = getString(getContext(), KEY_PICK_MODE_BUCKET_BOTTOM);
        String[] tempStart = end.split("\\.");
        if (tempStart.length == 3) {
            bottomValue[0] = parseInt(tempStart[0]);
            bottomValue[1] = parseInt(tempStart[1]);
            bottomValue[2] = parseInt(tempStart[2]);
        }

        mBucketRange = getInt(getContext(), KEY_PHOTO_BUCKET_START_MODE);

        //map range
        top = getLong(getContext(), KEY_MAP_VALUE_TOP);
        bottom = getLong(getContext(), KEY_MAP_VALUE_BOTTOM);
        left = getLong(getContext(), KEY_MAP_VALUE_LEFT);
        right = getLong(getContext(), KEY_MAP_VALUE_RIGHT);


        DataBaseHelper mDB = getInstance();
        //여기 수정할것 app.photo 사용
        ArrayList<Photo> photo = mDB.collectPhotoWithQuery(SELECT_PHOTO_ALL);

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
                        if (IsDateEquals(topValue, getDisplayDate(photo.get(position).getDate()))) {
                            e("Find same");
                            bFindSame = true;
                            break;
                        } else {
                            e("Skip : " + position);
                            position++;
                        }
                        if (failure) {
                            e("Failure1");
                            return false;
                        }
                    } while (position < photo.size());

                    if (bFindSame) {
                        break;
                    }
                    position = 0;
                    changeHeadValue();
                    if (failure) {
                        e("Failure2");
                        return false;
                    }
                } while (!bIsExit);

                ArrayList<Integer> list = new ArrayList<>();
                ArrayList<LatLng> latlng = new ArrayList<>();
                int addIndex = position;
                boolean bGroupingExit = false;
                do {
                    Photo tempPhoto = photo.get(addIndex);
                    int[] tempDate = getDisplayDate(tempPhoto.getDate());

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
                    if (failure) {
                        e("Failure3");
                        return false;
                    }
                } while (addIndex < photo.size() && !bGroupingExit);

                if (list.size() > 0) {
                    resultItem = new PhotoBucketItem();
                    resultItem.setDate(topValue[0] + "." + topValue[1] + "." + topValue[2]);
                    resultItem.setRegion(latlng);
                    resultItem.setCount(valueOf(list.size()));
                    resultItem.setPhotoItem(list);
                    publishProgress(resultItem);
                }

                position = addIndex;

                changeHeadValue();

            } while (position < photo.size() && !bIsExit);
            return true;
        } else {
            e("There is no photo");
            return false;
        }


    }

    @Override
    protected void onProgressUpdate(PhotoBucketItem... items) {
        e("Publish PhotoBucket");
        getBus().post(new PhotoBucketCompleteEvent(items[0]));
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            getBus().post(new ErrorEvent("Failed to get the information from MediaStore"));
        }
    }

    private void changeHeadValue() {
        /**
         * Ugly calculation : decrease date according to the display mode
         */
        if (mBucketRange == RANGE_YEAR) {
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
        e(tag + " / Date : " + Integer.toString(date[0]) + "." + Integer.toString(date[1]) + "." + Integer.toString(date[2]));
    }

    private boolean IsDateEquals(int[] base, int[] comp) {
        if (base[0] == comp[0] && base[1] == comp[1] && base[2] == comp[2]) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isMapIncluded(double lat, double lng) {
        if ((top > lat && left < lng) && (bottom < lat && right > lng)) {
            return true;
        } else {
            getBus().post(new ErrorEvent("Invalid status"));
        }
        return false;
    }
}
