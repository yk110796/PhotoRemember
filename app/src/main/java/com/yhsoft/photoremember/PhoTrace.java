package com.yhsoft.photoremember;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.Preference;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.MEDIA_UNMOUNTED;
import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;
import static com.yhsoft.photoremember.NetworkStatusChecker.checkNetwork;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_YEAR;
import static com.yhsoft.photoremember.util.Preference.KEY_APP_FIRST_START;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_BUCKET_START_MODE;
import static com.yhsoft.photoremember.util.Preference.putInt;

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
        String ext = getExternalStorageState();
        if (ext.equals(MEDIA_MOUNTED)) {
            mSdPath = getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            mSdPath = MEDIA_UNMOUNTED;
        }

        File db_dir = new File(mSdPath + "/photrace_db");
        if (!db_dir.isDirectory()) {
            db_dir.mkdirs();
        }
        db_path = mSdPath + "/photrace_db";

        checkNetwork(this);
        putInt(this, KEY_PHOTO_BUCKET_START_MODE, RANGE_YEAR);
        putInt(this, KEY_APP_FIRST_START, FIRSTISZERO);


    }

    public static void setNetworkStatus(boolean status) {
        bNetwrokConnected = status;
    }

    public static boolean checkNetworkConnected() {
        return bNetwrokConnected;
    }

    public void clearArrayLists() {
        latArray.clear();
        lngArray.clear();
        photoArray.clear();
        dateArray.clear();

    }

}
