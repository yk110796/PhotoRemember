/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.Preference;

import java.io.File;
import java.util.ArrayList;

public class PhoTrace extends Application {

    private static Context mContext;
    private static volatile boolean bNetwrokConnected;
    public static final int FIRSTISZERO = 0;

    public static Context getContext() {
        return mContext;
    }

    String mSdPath = null; // SD카드 절대경로(디렉토리)
    public String db_path = null;
    public ArrayList<Photo> photo = new ArrayList<>();
    public double top, right, bottom, left;
    public long leftInMillis, rightInMillis;
    public int tlview_height;

    public int photoTotalNum; // to set total photo count on textview under the timeslider

    public static ArrayList<Double> latArray = new ArrayList<>(); //this array using in photoDetailActivity and PhotoViewFragment(for setting address in overlay view)
    public static ArrayList<Double> lngArray = new ArrayList<>(); //this array using in photoDetailActivity and PhotoViewFragment(for setting address in overlay view)
    public static ArrayList<Integer> photoArray = new ArrayList<>(); //this array is photo id array and using in photoDetailActivity and PhotoViewFragment(for swiping left, right)
    public static ArrayList<Long> dateArray = new ArrayList<>(); //this array using in photoDetailActivity and PhotoViewFragment(for setting photo date in overlay view)

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            mSdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            mSdPath = Environment.MEDIA_UNMOUNTED;
        }

        File db_dir = new File(mSdPath + "/photrace_db");
        if (!db_dir.isDirectory()) {
            db_dir.mkdirs();
        }
        db_path = mSdPath + "/photrace_db" ;

        NetworkStatusChecker.checkNetwork(this);
        Preference.putInt(this, Preference.KEY_PHOTO_BUCKET_START_MODE, DateUtil.RANGE_YEAR);
        Preference.putInt(this, Preference.KEY_APP_FIRST_START, FIRSTISZERO);


    }

    public static void setNetworkStatus(boolean status) {
        bNetwrokConnected = status;
    }

    public static boolean checkNetworkConnected() {
        return bNetwrokConnected;
    }

    public void clearArrayLists(){
        latArray.clear();
        lngArray.clear();
        photoArray.clear();
        dateArray.clear();

    }

}
