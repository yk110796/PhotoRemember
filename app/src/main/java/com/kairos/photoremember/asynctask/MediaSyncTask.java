/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.database.DataBaseHelper;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.event.MediaSyncCompleteEvent;
import com.kairos.photoremember.util.BusProvider;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.DebugLog;
import com.kairos.photoremember.util.Global;

import java.io.File;
import java.io.IOException;

/**
 * MediaSyncTask
 * Media synchronization module when PhoTrace is started up
 * Currently, we just replace the existing database newly
 * TODO Media Scan and Update Datebase
 */
public class MediaSyncTask extends AsyncTask<Void, Void, Boolean> {

    private Cursor mImageCursor;
    private Context mContext;
    private DataBaseHelper mDB = null;
    PhoTrace app;


    public MediaSyncTask() {
        mContext = PhoTrace.getContext();
        mDB = DataBaseHelper.getInstance();
        app = (PhoTrace) mContext.getApplicationContext();



    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
        };
        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " ASC";



        mImageCursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, orderBy);
      //  mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null);

        if (mImageCursor != null && mImageCursor.moveToFirst()){
            try {
                mDB.beginTransaction();
                do {
                    String f = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    DebugLog.d("CURSOR RESULT : "+f);
                    File image = new File(mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    if(image.exists()){
                        DebugLog.d("EXIST : "+f);
                    }else{
                        DebugLog.d("NOT EXIST : "+f);
                    }
                    try {
                        if (image.exists()) {
                            ContentValues data = new ContentValues();

                            data.putNull(DataBaseHelper.COMMON_ID);
                            int id = mImageCursor.getInt(mImageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                            if (DataBaseHelper.getInstance().checkIfNoPhoto(id)) {
                                data.put(Photo.ID, id);

                                ExifInterface info = new ExifInterface(image.toString());
                                if (info != null) {
                                    long createTime = DateUtil.getDateInMIllis(info.getAttribute(ExifInterface.TAG_DATETIME));
                                    if (createTime > 0) {
                                        data.put(Photo.DATE, createTime);
                                        DebugLog.e("ID : " + id + " / DATE : " + createTime);
                                    } else {
                                        data.put(Photo.DATE, image.lastModified());
                                        DebugLog.e("ID : " + id + " / DATE : " + image.lastModified());
                                    }

                                    float[] latlng = new float[2];
                                    boolean result = info.getLatLong(latlng);
                                    if (result) {
                                        if (latlng[0] == 0 && latlng[1] == 0) {
                                            data.put(Photo.LAT, Global.DEFAULT_LAT);
                                            data.put(Photo.LNG, Global.DEFAULT_LNG);
                                        } else {
                                            data.put(Photo.LAT, latlng[0]);
                                            data.put(Photo.LNG, latlng[1]);
                                        }
                                    } else {
                                        Double lat = mImageCursor.getDouble(mImageCursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
                                        Double lng = mImageCursor.getDouble(mImageCursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                                        if ((lat >= -90 && lat <= 90) && (lng >= -180 && lng <= 180)) {
                                            if (lat == 0 && lng == 0) {
                                                data.put(Photo.LAT, Global.DEFAULT_LAT);
                                                data.put(Photo.LNG, Global.DEFAULT_LNG);
                                            } else {
                                                data.put(Photo.LAT, lat);
                                                data.put(Photo.LNG, lng);
                                            }
                                        } else {
                                            data.put(Photo.LAT, Global.DEFAULT_LAT);
                                            data.put(Photo.LNG, Global.DEFAULT_LNG);
                                        }
                                    }
                                } else {
                                    data.put(Photo.DATE, image.lastModified());
                                    Double lat = mImageCursor.getDouble(mImageCursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
                                    Double lng = mImageCursor.getDouble(mImageCursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                                    if ((lat >= -90 && lat <= 90) && (lng >= -180 && lng <= 180)) {
                                        data.put(Photo.LAT, lat);
                                        data.put(Photo.LNG, lng);
                                    } else {
                                        data.put(Photo.LAT, Global.DEFAULT_LAT);
                                        data.put(Photo.LNG, Global.DEFAULT_LNG);
                                    }
                                }

                                data.putNull(Photo.TITLE);
                                data.putNull(Photo.DESCRIPTION);

                                data.put(Photo.PLACE, -1);

                                mDB.insertData(data, DataBaseHelper.TABLE_NAME_PHOTO);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } while (mImageCursor.moveToNext());
                mDB.setTransactionSuccess();

            } finally {
                mDB.endTransaction();
            }
            mImageCursor.close();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        DebugLog.e("MediaSyncTask onPostExecute");
        PhoTrace app = (PhoTrace) mContext.getApplicationContext();
        //arraylist로 메모리에 저장
        app.photo = mDB.collectPhotoWithQuery(mDB.SELECT_PHOTO_ALL);
        BusProvider.getBus().post(new MediaSyncCompleteEvent(result));
    }
}
