package com.yhsoft.photoremember.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.database.DataBaseHelper;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.event.MediaSyncCompleteEvent;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.Global;

import java.io.File;
import java.io.IOException;

import static android.media.ExifInterface.TAG_DATETIME;
import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.ImageColumns.DATE_TAKEN;
import static android.provider.MediaStore.Images.ImageColumns.LATITUDE;
import static android.provider.MediaStore.Images.ImageColumns.LONGITUDE;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.MediaColumns.DATA;
import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.database.DataBaseHelper.COMMON_ID;
import static com.yhsoft.photoremember.database.DataBaseHelper.SELECT_PHOTO_ALL;
import static com.yhsoft.photoremember.database.DataBaseHelper.TABLE_NAME_PHOTO;
import static com.yhsoft.photoremember.database.DataBaseHelper.getInstance;
import static com.yhsoft.photoremember.database.model.Photo.DATE;
import static com.yhsoft.photoremember.database.model.Photo.DESCRIPTION;
import static com.yhsoft.photoremember.database.model.Photo.ID;
import static com.yhsoft.photoremember.database.model.Photo.LAT;
import static com.yhsoft.photoremember.database.model.Photo.LNG;
import static com.yhsoft.photoremember.database.model.Photo.PLACE;
import static com.yhsoft.photoremember.database.model.Photo.TITLE;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DateUtil.getDateInMIllis;
import static com.yhsoft.photoremember.util.DebugLog.d;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.Global.DEFAULT_LAT;
import static com.yhsoft.photoremember.util.Global.DEFAULT_LNG;

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
        mContext = getContext();
        mDB = getInstance();
        app = (PhoTrace) mContext.getApplicationContext();


    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String[] projection = {
                _ID,
                DATA,
                LATITUDE,
                LONGITUDE
        };
        String orderBy = DATE_TAKEN + " ASC";


        mImageCursor = mContext.getContentResolver().query(
                EXTERNAL_CONTENT_URI, projection,
                null, null, orderBy);
        //  mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null);

        if (mImageCursor != null && mImageCursor.moveToFirst()) {
            try {
                mDB.beginTransaction();
                do {
                    String f = mImageCursor.getString(mImageCursor.getColumnIndex(DATA));
                    d("CURSOR RESULT : " + f);
                    File image = new File(mImageCursor.getString(mImageCursor.getColumnIndex(DATA)));
                    if (image.exists()) {
                        d("EXIST : " + f);
                    } else {
                        d("NOT EXIST : " + f);
                    }
                    try {
                        if (image.exists()) {
                            ContentValues data = new ContentValues();

                            data.putNull(COMMON_ID);
                            int id = mImageCursor.getInt(mImageCursor.getColumnIndex(_ID));
                            if (getInstance().checkIfNoPhoto(id)) {
                                data.put(ID, id);

                                ExifInterface info = new ExifInterface(image.toString());
                                if (info != null) {
                                    long createTime = getDateInMIllis(info.getAttribute(TAG_DATETIME));
                                    if (createTime > 0) {
                                        data.put(DATE, createTime);
                                        e("ID : " + id + " / DATE : " + createTime);
                                    } else {
                                        data.put(DATE, image.lastModified());
                                        e("ID : " + id + " / DATE : " + image.lastModified());
                                    }

                                    float[] latlng = new float[2];
                                    boolean result = info.getLatLong(latlng);
                                    if (result) {
                                        if (latlng[0] == 0 && latlng[1] == 0) {
                                            data.put(LAT, DEFAULT_LAT);
                                            data.put(LNG, DEFAULT_LNG);
                                        } else {
                                            data.put(LAT, latlng[0]);
                                            data.put(LNG, latlng[1]);
                                        }
                                    } else {
                                        Double lat = mImageCursor.getDouble(mImageCursor.getColumnIndex(LATITUDE));
                                        Double lng = mImageCursor.getDouble(mImageCursor.getColumnIndex(LONGITUDE));
                                        if ((lat >= -90 && lat <= 90) && (lng >= -180 && lng <= 180)) {
                                            if (lat == 0 && lng == 0) {
                                                data.put(LAT, DEFAULT_LAT);
                                                data.put(LNG, DEFAULT_LNG);
                                            } else {
                                                data.put(LAT, lat);
                                                data.put(LNG, lng);
                                            }
                                        } else {
                                            data.put(LAT, DEFAULT_LAT);
                                            data.put(LNG, DEFAULT_LNG);
                                        }
                                    }
                                } else {
                                    data.put(DATE, image.lastModified());
                                    Double lat = mImageCursor.getDouble(mImageCursor.getColumnIndex(LATITUDE));
                                    Double lng = mImageCursor.getDouble(mImageCursor.getColumnIndex(LONGITUDE));
                                    if ((lat >= -90 && lat <= 90) && (lng >= -180 && lng <= 180)) {
                                        data.put(LAT, lat);
                                        data.put(LNG, lng);
                                    } else {
                                        data.put(LAT, DEFAULT_LAT);
                                        data.put(LNG, DEFAULT_LNG);
                                    }
                                }

                                data.putNull(TITLE);
                                data.putNull(DESCRIPTION);

                                data.put(PLACE, -1);

                                mDB.insertData(data, TABLE_NAME_PHOTO);
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
        e("MediaSyncTask onPostExecute");
        PhoTrace app = (PhoTrace) mContext.getApplicationContext();
        //arraylist로 메모리에 저장
        app.photo = mDB.collectPhotoWithQuery(SELECT_PHOTO_ALL);
        getBus().post(new MediaSyncCompleteEvent(result));
    }
}
