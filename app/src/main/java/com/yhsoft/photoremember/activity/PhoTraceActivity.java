package com.yhsoft.photoremember.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.yhsoft.photoremember.database.DataBaseHelper;
import com.yhsoft.photoremember.event.FragmentSwitchEvent;
import com.yhsoft.photoremember.event.HeaderActionEvent;
import com.yhsoft.photoremember.event.PhotoDetailEvent;
import com.yhsoft.photoremember.fragment.MyTimeTraceFragment;
import com.yhsoft.photoremember.fragment.PhotoBucketFragment;
import com.yhsoft.photoremember.fragment.photodetail.PhotoDetailFragment;

import java.util.ArrayList;
import java.util.Locale;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.DATE_TAKEN;
import static android.provider.MediaStore.Images.ImageColumns.LATITUDE;
import static android.provider.MediaStore.Images.ImageColumns.LONGITUDE;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.support.v4.app.Fragment.instantiate;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.View.OnClickListener;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.R.id.frame_container;
import static com.yhsoft.photoremember.R.layout.activity_drawer_base;
import static com.yhsoft.photoremember.R.string.title_my_profile;
import static com.yhsoft.photoremember.R.string.title_mytimetrace;
import static com.yhsoft.photoremember.R.string.title_service_info;
import static com.yhsoft.photoremember.R.string.title_settings;
import static com.yhsoft.photoremember.database.DataBaseHelper.getInstance;
import static com.yhsoft.photoremember.debug.ViewServer.get;
import static com.yhsoft.photoremember.event.HeaderActionEvent.MapAction.OPEN_MULTIPLE;
import static com.yhsoft.photoremember.event.HeaderActionEvent.MapAction.OPEN_SINGLE;
import static com.yhsoft.photoremember.event.HeaderActionEvent.SelectedAction.ACTION_BACK;
import static com.yhsoft.photoremember.event.HeaderActionEvent.SelectedAction.ACTION_OPEN_MAP;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_DAY;
import static com.yhsoft.photoremember.util.DateUtil.getDateString;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.Global.FRAGMENT_PHOTO_BUCKET;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_DATE_PICK_MODE;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_BOTTOM_STATIC;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_TOP;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_TOP_STATIC;
import static com.yhsoft.photoremember.util.Preference.KEY_TIMEBAR_RANGE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_TIMEBAR_RANGE_TOP;
import static com.yhsoft.photoremember.util.Preference.putBoolean;
import static com.yhsoft.photoremember.util.Preference.putLong;

public class PhoTraceActivity extends AppCompatActivity implements OnClickListener {
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

    public void setFailure(boolean a) {
        failure = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_drawer_base);

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
                SOFT_INPUT_ADJUST_RESIZE);

        //  mSideManu = new ResideMenu(this);
        //  mSideManu.attachToActivity(this);

        int titles[] = {
                title_mytimetrace,
                title_service_info,
                title_settings,
                title_my_profile,
        };

//        for (int i = 0; i < titles.length; i++) {
//            ResideMenuItem item = new ResideMenuItem(this, R.drawable.ic_launcher, titles[i]);
//            item.setOnClickListener(this);
//            item.setId(titles[i]);
//            mSideManu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
//        }
//        mSideManu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
//        mSideManu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        final Fragment contentFragment = instantiate(this,
                MyTimeTraceFragment.class.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .add(frame_container, contentFragment, contentFragment.getClass().getName())
                .commit();

        get(this).addWindow(this);
        initPhotoDateRange();
        /**
         * Always start with normal mode
         */
        putBoolean(getContext(), KEY_PHOTO_DATE_PICK_MODE, false);

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
        String sortOrder = DATE_TAKEN + " ASC";

        String[] projection = {
                _ID,
                // MediaStore.Images.Media.DATA,
                LATITUDE,
                LONGITUDE,
                DATE_TAKEN
        };

        Cursor cursor = getContentResolver().query(
                EXTERNAL_CONTENT_URI,
                projection, null,
                null, sortOrder);


        if (cursor.moveToFirst())
            dateMin = cursor.getLong(3);

        if (cursor.moveToLast()) {
            dateMax = cursor.getLong(3);
        }


        putLong(getContext(), KEY_PHOTO_RANGE_BOTTOM, dateMin);
        putLong(getContext(), KEY_PHOTO_RANGE_TOP, dateMax);

        putLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM, dateMin);
        putLong(getContext(), KEY_TIMEBAR_RANGE_TOP, dateMax);

        putLong(getContext(), KEY_PHOTO_RANGE_BOTTOM_STATIC, dateMin);
        putLong(getContext(), KEY_PHOTO_RANGE_TOP_STATIC, dateMax);


        e("bottom string : " + getDateString(dateMin, RANGE_DAY));
        e("top string : " + getDateString(dateMax, RANGE_DAY));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("PhoTraceActivity", " OnResume");
        get(this).setFocusedWindow(this);

        getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getBus().unregister(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataBaseHelper mHelper = getInstance();
        if (mHelper != null) {
            mHelper.closeDataBase();
        }
        super.onDestroy();
        setFailure(true);
        get(this).removeWindow(this);
    }

    @Override
    public void onBackPressed() {
        e("BackStack Entry Count : " + getSupportFragmentManager().getBackStackEntryCount());
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
        e("Start fragment_my transition");
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
        if (event.getAction() == ACTION_OPEN_MAP) {
            if (photoArray.size() > 0) {
                photoArray.clear();
            }
            if (event.getMapAction() == OPEN_SINGLE) {
                e("Single ID : " + event.getPhotoID());
                photoArray.add(event.getPhotoID());
            } else if (event.getMapAction() == OPEN_MULTIPLE) {
                PhotoBucketFragment bucketFragment = (PhotoBucketFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_PHOTO_BUCKET);
                photoArray = bucketFragment.getCurrentBucket();
            }
            e("Bucket size : " + photoArray.size());
            if (photoArray.size() > 0) {
                //Fragment target = TraceMapFragment.newInstance(photoArray);
                // switchActiveView(target, TRANSACTION_MODE_NORMAL);
            } else {
                e("Can't reach to the Photo bucket fragment_my");
            }


        } else if (event.getAction() == ACTION_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    private final void switchActiveView(final Fragment fragment, int mode) {
        int containerID = frame_container;
        final FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();

        switch (mode) {
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
            switch (id) {
                case title_mytimetrace:
                    target = instantiate(this,
                            MyTimeTraceFragment.class.getName());
                    break;
                case title_settings:
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
        if (keyCode == KEYCODE_BACK) {
            if (!mFlag) {
                makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.",
                        LENGTH_SHORT).show();
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
