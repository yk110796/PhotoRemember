/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.R;
import com.kairos.photoremember.database.DataBaseHelper;
import com.kairos.photoremember.debug.ViewServer;
import com.kairos.photoremember.event.FragmentSwitchEvent;
import com.kairos.photoremember.event.HeaderActionEvent;
import com.kairos.photoremember.event.PhotoDetailEvent;
import com.kairos.photoremember.fragment.MyTimeTraceFragment;
import com.kairos.photoremember.fragment.PhotoBucketFragment;
import com.kairos.photoremember.fragment.photodetail.PhotoDetailFragment;
import com.kairos.photoremember.util.BusProvider;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.DebugLog;
import com.kairos.photoremember.util.Global;
import com.kairos.photoremember.util.Preference;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

public class PhoTraceActivity extends FragmentActivity implements View.OnClickListener {
    public static final int TRANSACTION_MODE_NORMAL = 1;
    public static final int TRANSACTION_MDOE_CHANGE = 2;
    public static final int TRANSACTION_MODE_ADD = 3;

    private int mCurrentFragment;

    public static boolean failure = false;
    /**
     * Used for Map Trace
     */
    private ArrayList<Integer> photoArray = new ArrayList<>();

    private Handler mHandler; //백키 종료를 위한 핸들러
    private boolean mFlag = false; //백키 종료를 위한 플래그

    public void setFailure(boolean a){
        failure = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_base);

        Locale systemLocale = getResources().getConfiguration().locale;

       mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mFlag = false;
                }
            }
        };

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );

      //  mSideManu = new ResideMenu(this);
      //  mSideManu.attachToActivity(this);

        int titles[] = {
                R.string.title_mytimetrace,
                R.string.title_service_info,
                R.string.title_settings,
                R.string.title_my_profile,
        };

//        for (int i = 0; i < titles.length; i++) {
//            ResideMenuItem item = new ResideMenuItem(this, R.drawable.ic_launcher, titles[i]);
//            item.setOnClickListener(this);
//            item.setId(titles[i]);
//            mSideManu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
//        }
//        mSideManu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
//        mSideManu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        final Fragment contentFragment = Fragment.instantiate(this,
                MyTimeTraceFragment.class.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, contentFragment, contentFragment.getClass().getName())
                .commit();

     ViewServer.get(this).addWindow(this);
     initPhotoDateRange();
        /**
         * Always start with normal mode
         */
        Preference.putBoolean(PhoTrace.getContext(), Preference.KEY_PHOTO_DATE_PICK_MODE, false);

        }

     /**
     * Initialize the time range whenever bring up
     */

    private void initPhotoDateRange() {
        // ArrayList<Long> dateList = DataBaseHelper.getInstance().selectPhotoDate();
        // long dateMin = Collections.min(dateList);
        // long dateMax = Collections.max(dateList);
        long dateMin = 0;
        long dateMax = 0;
        String sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC";

        String[] projection = {
                MediaStore.Images.Media._ID,
                // MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_TAKEN
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null,
                null, sortOrder );


        if(cursor.moveToFirst())
            dateMin = cursor.getLong(3);

        if(cursor.moveToLast()){
            dateMax = cursor.getLong(3);
        }


        Preference.putLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM, dateMin);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP, dateMax);

        Preference.putLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM, dateMin);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP, dateMax);

        Preference.putLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM_STATIC, dateMin);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP_STATIC, dateMax);


        DebugLog.e("bottom string : " + DateUtil.getDateString(dateMin, DateUtil.RANGE_DAY));
        DebugLog.e("top string : " + DateUtil.getDateString(dateMax, DateUtil.RANGE_DAY));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("PhoTraceActivity", " OnResume");
        ViewServer.get(this).setFocusedWindow(this);

            BusProvider.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

            BusProvider.getBus().unregister(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataBaseHelper mHelper = DataBaseHelper.getInstance();
        if (mHelper != null) {
            mHelper.closeDataBase();
        }
        super.onDestroy();
        setFailure(true);
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onBackPressed() {
        DebugLog.e("BackStack Entry Count : " + getSupportFragmentManager().getBackStackEntryCount());
//        if (mSideManu.isOpened()) {
//            mSideManu.closeMenu();
//        } else {
            super.onBackPressed();
 //       }

    }
    /**
     * Event Subscriber
     */

    /**
     * Replace to specific UI fragment_my
     */
    @Subscribe
    public void onFragmentSwitchEvent(FragmentSwitchEvent event) {
        switchActiveView(event.getFragment(), event.getReplace());
        DebugLog.e("Start fragment_my transition");
    }

    /**
     * Events occurred by the Header
     * ACTION_OPEN_MENU : Open NavigationDrawer
     * ACTION_OPEN_MAP : Open Location Picker Map
     * ACTION_BACK : Back to previous Fragment
     * More..
     */




    @Subscribe
    public void onHeaderActionEvent(HeaderActionEvent event) {
        if (event.getAction() == HeaderActionEvent.SelectedAction.ACTION_OPEN_MAP) {
            if (photoArray.size() > 0) {
                photoArray.clear();
            }
            if (event.getMapAction() == HeaderActionEvent.MapAction.OPEN_SINGLE) {
                DebugLog.e("Single ID : " + event.getPhotoID());
                photoArray.add(event.getPhotoID());
            } else if (event.getMapAction() == HeaderActionEvent.MapAction.OPEN_MULTIPLE) {
                PhotoBucketFragment bucketFragment = (PhotoBucketFragment) getSupportFragmentManager().findFragmentByTag(Global.FRAGMENT_PHOTO_BUCKET);
                photoArray = bucketFragment.getCurrentBucket();
            }
            DebugLog.e("Bucket size : " + photoArray.size());
            if (photoArray.size() > 0) {
                //Fragment target = TraceMapFragment.newInstance(photoArray);
               // switchActiveView(target, TRANSACTION_MODE_NORMAL);
            } else {
                DebugLog.e("Can't reach to the Photo bucket fragment_my");
            }



        } else if (event.getAction() == HeaderActionEvent.SelectedAction.ACTION_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }





    private final void switchActiveView(final Fragment fragment, int mode) {
        int containerID = R.id.frame_container;
        final FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();

        switch(mode) {
            case TRANSACTION_MODE_NORMAL:
                transaction
                        .replace(containerID, fragment, fragment.getClass().getName())
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
                break;
            case TRANSACTION_MDOE_CHANGE:
                transaction
                        .replace(containerID, fragment, fragment.getClass().getName())
                        .commit();
                break;
        }
    }

    /**
     * Switch screen to Photo Detail
     */
    @Subscribe
    public void onPhotoDetailEvent(PhotoDetailEvent event) {
        Fragment target = PhotoDetailFragment.getInstance(event.getID(), event.getPhotoList());
        switchActiveView(target, TRANSACTION_MODE_NORMAL);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Fragment target = null;
        if (id != mCurrentFragment) {
            switch(id) {
                case R.string.title_mytimetrace:
                    target = Fragment.instantiate(this,
                            MyTimeTraceFragment.class.getName());
                    break;
                case R.string.title_settings:
                    break;
            }
            mCurrentFragment = id;
        }
        if (target != null) {
            switchActiveView(target, TRANSACTION_MDOE_CHANGE);
        }
       // mSideManu.closeMenu(); ///////////
    }

    /*
     * 백키 이벤트를 가로채서 플래그값 확인 후 처리. 플래그 값이 true인
     * 상태에서 2초 이내에 백키를 누르면 액티비티 종료.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mFlag) {
                Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.",
                        Toast.LENGTH_SHORT).show();
                mFlag = true;
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
