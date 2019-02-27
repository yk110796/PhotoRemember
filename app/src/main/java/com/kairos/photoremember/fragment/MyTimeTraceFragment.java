/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.R;
import com.kairos.photoremember.activity.MarkerPhotoDetailActivity2;
import com.kairos.photoremember.activity.SliderActivity;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.database.model.PhotoMarker;
import com.kairos.photoremember.event.Action_UpEvent;
import com.kairos.photoremember.event.onClusteringEvent;
import com.kairos.photoremember.event.onClusteringEventForMap;
import com.kairos.photoremember.fragment.map.MultiDrawable;
import com.kairos.photoremember.util.BusProvider;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.DebugLog;
import com.kairos.photoremember.util.MediaUtil;
import com.kairos.photoremember.util.Preference;
import com.kairos.photoremember.view.TLView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyTimeTraceFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<PhotoMarker>,
        ClusterManager.OnClusterInfoWindowClickListener<PhotoMarker>,
        ClusterManager.OnClusterItemClickListener<PhotoMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PhotoMarker> {

    @InjectView(R.id.header_center_button)
    ImageButton mCenter;
    @InjectView(R.id.header_right_button)
    ImageButton mResultButton;
    @InjectView(R.id.header_left_button)
    ImageButton mBackButton;
    @InjectView(R.id.my_map_container)
    LinearLayout mMapContainer;

    @InjectView(R.id.bucket_container)
    FrameLayout mPhotoBucketFrame;

    @InjectView(R.id.map_button)
    ImageView mMapButton;
    @InjectView(R.id.tl_overlay)
    FrameLayout mTLviewOverlay;
    @InjectView(R.id.tlview_container)
    FrameLayout mTLviewContainer;

    @InjectView(R.id.timeslider_text)
    TextView mTimebarText;
    private View rootView = null;


    PhotoBucketFragment photoFragment = null;

    PhotoViewFragment cursorFrag = null;
    PhotoViewFragment.GridCursorAdapter adapter = null;


    /**
     * Used to check the mode whether the specific date mode is activated or not
     */
    public static boolean bIsPickerMode = false;
    private boolean bIncreaseStart = false;
    private boolean bDecreaseStart = false;

    private final static int TIME_RANGE_TO_YEAR = 0;
    private final static int TIME_RANGE_TO_DAY = 1;
    private final static int TIME_CHANGE_INTERVAL = ViewConfiguration.getLongPressTimeout();
    private final static int SCALED_TOUCH_SLOP = ViewConfiguration.get(PhoTrace.getContext()).getScaledTouchSlop();

    /*map*/
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    ArrayList<Integer> AllList = new ArrayList<>();
    ArrayList<Integer> photoArray = new ArrayList<>();
    private HashMap<String, Integer> markerMap = new HashMap<>();


    ArrayList<PhotoMarker> mPhotos = new ArrayList<>();
    ArrayList<Integer> mLocationphotoArray = new ArrayList<>();

    private ClusterManager<Photo> mClusterManager;
    private ClusterManager<PhotoMarker> mPhotoMarkerClusterManager;
    private Random mRandom = new Random(1984);

    int mapSizeInitTop = 90;
    int mapSizeInitBottom = -90;
    int mapSizeInitRight = 180;
    int mapSizeInitLeft = -180;


    //mapview animation
    ViewGroup.LayoutParams params;
    ViewGroup.LayoutParams params_container;
    ViewGroup.LayoutParams params_photo_bucket_container;
    ViewGroup.LayoutParams params_tlview_container;
    int bucket_size, map_size, map_container_size, tlview_container_size = 0;
    int map_flag = 0;

    /* to vibrate */
    Vibrator m_vibrator = null;
    private TLView timeSliderView;

    static private boolean MAP_VISIBLE_MODE = false;
    static public boolean TLVIEW_CLICK_MODE = false;
    static private boolean TLVIEW_MAP_MOVE = false;

    private Handler mHandler;
    private Runnable mRunnable;

    private ProgressDialog prg;

    private final String TAG = this.getClass().getSimpleName();

    Context mContext;
    PhoTrace app;

    private static boolean firstOrNot = false;
    public static boolean timeSliderFlag = false;

    private ImageView mBubbleImageView;
    private PopupWindow mPopupWindow;
    private int[] bubbleImageId = {R.drawable.bubble5, R.drawable.bubble6};
    private int bubbleId = 0;
    public static final int NOTFIRST = 1;

    int move_count = 0;

    // TODO move timer thing to view itself
    Handler timeLinehandler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            timeSliderView.postInvalidate();
            timeLinehandler.postDelayed(runnable, 250);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
        timeLinehandler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);
        runnable.run();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.fragment_mytimetrace, container, false);

        timeSliderView = (TLView) rootView.findViewById(R.id.tl_content);
        ButterKnife.inject(this, rootView);

        m_vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mCenter.setImageResource(R.drawable.logo);
        mResultButton.setImageResource(R.drawable.result);
        mMapButton.setImageResource(R.drawable.plus);
        mTLviewOverlay.setVisibility(View.VISIBLE);

        prg = new ProgressDialog(getActivity());
        prg.setMessage("Loading your picture...");
        prg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prg.setCancelable(false);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.my_map);
        mMapFragment.getMapAsync(this);


        if (mMapFragment != null) {
            params = mMapFragment.getView().getLayoutParams();
            params_container = mMapContainer.getLayoutParams();
            params_photo_bucket_container = mPhotoBucketFrame.getLayoutParams();
            params_tlview_container = mTLviewContainer.getLayoutParams();
        }

        mMapContainer.post(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("map_container :" + mMapContainer.getHeight());
                map_container_size = mMapContainer.getHeight();
            }
        });

        mPhotoBucketFrame.post(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("map_container :" + mPhotoBucketFrame.getHeight());
                bucket_size = mPhotoBucketFrame.getHeight();
            }
        });

        mTLviewContainer.post(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("map_container :" + mTLviewContainer.getHeight());
                tlview_container_size = mTLviewContainer.getHeight();
                app.tlview_height = tlview_container_size;
            }
        });

        mMapFragment.getView().post(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("map_container :" + mMapFragment.getView().getHeight());
                map_size = mMapFragment.getView().getHeight();
            }
        });


        Preference.putLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_LEFT, mapSizeInitLeft);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_RIGHT, mapSizeInitRight);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_TOP, mapSizeInitTop);
        Preference.putLong(PhoTrace.getContext(), Preference.KEY_MAP_VALUE_BOTTOM, mapSizeInitBottom);

        photoArray.clear();
        initializeRange();

        cursorFrag = (PhotoViewFragment) Fragment.instantiate(getActivity(), PhotoViewFragment.class.getName());
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.bucket_container   , cursorFrag, "main_fragment").commit() ;


        int firstAppStart = Preference.getInt(getActivity(), Preference.KEY_APP_FIRST_START);
        if (firstAppStart == 0) {
            showActivityOverlay();
        }else{

        }

        return rootView;
    }

    private void showActivityOverlay() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.bubble_activity, null);
                mPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
                mPopupWindow.setAnimationStyle(-1);
//                mPopupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, 0);
                mBubbleImageView = (ImageView) popupView.findViewById(R.id.bubble_imageView);
                mBubbleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (bubbleImageId.length > bubbleId) {
                            mBubbleImageView.setImageResource(bubbleImageId[bubbleId]);
                            bubbleId++;
                        } else {
                            Preference.putInt(getActivity(), Preference.KEY_APP_FIRST_START, NOTFIRST);
                            mPopupWindow.dismiss();
                        }
                    }
                });
            }
        }, 100L);
    }

    private void initializeRange() {
        long dateMin = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM);
        long dateMax = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP);

        app.leftInMillis = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM);
        app.rightInMillis = Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP);
        if (dateMin == Long.MIN_VALUE || dateMax == Long.MIN_VALUE) {
            dateMin = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM);
            dateMax = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP);
            app.leftInMillis = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM);
            app.rightInMillis = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP);
            setTimebarRange(dateMin, dateMax);
        }

        int mode = Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE);

        DebugLog.e("bottom string : " + DateUtil.getDateString(dateMin, DateUtil.RANGE_DAY));
        DebugLog.e("top string : " + DateUtil.getDateString(dateMax, DateUtil.RANGE_DAY));


        /**
         * Always start with normal mode
         */
        Preference.putBoolean(PhoTrace.getContext(), Preference.KEY_PHOTO_DATE_PICK_MODE, false);
        bIsPickerMode = false;
    }

        //시간바의 날짜를 세팅한다
    private void setTimebarRange(long bottom, long top) {
        if (bottom != Long.MIN_VALUE) {
            Preference.putLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM, bottom);
            //app.leftInMillis = bottom;
        }
        if (top != Long.MIN_VALUE) {
            Preference.putLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP, top);
        }
        mTimebarText.setText(DateUtil.getDateString(bottom, DateUtil.RANGE_DAY)
                + " - " + DateUtil.getDateString(top, DateUtil.RANGE_DAY) + "(" + app.photoTotalNum + ")");
        Log.e("Num1", "" + app.photoTotalNum);
    }


    private void setTimebarMode(int direction) {
        boolean dirtyFlag = false;
        int mode = Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE);

        switch (direction) {
            case TIME_RANGE_TO_YEAR:    // Left side button long press
                if (mode > DateUtil.RANGE_YEAR) {
                    --mode;
                    dirtyFlag = true;
                }
                break;
            case TIME_RANGE_TO_DAY:     // Right side button long press
                if (mode < DateUtil.RANGE_DAY) {
                    ++mode;
                    dirtyFlag = true;
                }
                break;
            default:
                DebugLog.e("Abnormal Case");
                break;
        }

        if (dirtyFlag) {
            Preference.putInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE, mode);
            String bottom = DateUtil.getDateString(
                    Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM),
                    mode);
            String top = DateUtil.getDateString(
                    Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP),
                    mode);
            //setTimebarText(bottom, top);

            reloadPhotoBucket();
        }
    }

    //***** 시간및 지역 변경후  다시 asynctask를 실행하는 중요 메서드
    private void reloadPhotoBucket() {
        if(cursorFrag != null)
            cursorFrag.reloadBucket();

}

    private void reloadPhotoBucketMap() {
        if(cursorFrag != null)
            cursorFrag.reloadBucketForMap();


}

    private void resetTimebarRange() {
        long dateMin = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_BOTTOM);
        long dateMax = Preference.getLong(PhoTrace.getContext(), Preference.KEY_PHOTO_RANGE_TOP);
        setTimebarRange(dateMin, dateMax);
        refreshAllScreen();
    }

    private void refreshAllScreen() {
        String bottom = DateUtil.getDateString(
                Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_BOTTOM),
                Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE));
        String top = DateUtil.getDateString(
                Preference.getLong(PhoTrace.getContext(), Preference.KEY_TIMEBAR_RANGE_TOP),
                Preference.getInt(PhoTrace.getContext(), Preference.KEY_PHOTO_BUCKET_START_MODE));
        //setTimebarText(bottom, top);
        reloadPhotoBucket();
    }

    private void showTimerangeAndSize() {
        mTimebarText.setText(DateUtil.getDateString(app.leftInMillis, DateUtil.RANGE_DAY)
                + " - " + DateUtil.getDateString(app.rightInMillis, DateUtil.RANGE_DAY) + "(" + app.photoTotalNum + ")");
        Log.e("Num2", "" + app.photoTotalNum);
    }

    /**
     * Date Pick Mode Methods
     * - If you always want to start with initial range,
     * just put the code snippet about PHOTO_RANGE_BOTTOM/TOP setting at the
     * PhoTraceActivity.onCreate() method
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            mMap.clear();
            UiSettings settings = mMap.getUiSettings();
            settings.setZoomControlsEnabled(false);
            mMap.setMyLocationEnabled(true);
            settings.setRotateGesturesEnabled(false);
            settings.setTiltGesturesEnabled(false);
            settings.setZoomControlsEnabled(true);
        }
        mClusterManager = new ClusterManager<Photo>(getActivity(), mMap);
        mPhotoMarkerClusterManager = new ClusterManager<PhotoMarker>(getActivity(), mMap);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(cameraPosition);
                mPhotoMarkerClusterManager.onCameraChange(cameraPosition);
                if (firstOrNot == false || TLVIEW_CLICK_MODE == true) {
                    if (firstOrNot == false)
                        firstOrNot = true;
                    else if (TLVIEW_CLICK_MODE == true)
                        TLVIEW_CLICK_MODE = false;
                } else {
                    getRegion();
                }
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnMarkerClickListener(mPhotoMarkerClusterManager);
    }


    public void getRegion() {
        DebugLog.e("map move");
        //map change mode save
        VisibleRegion vr = mMap.getProjection().getVisibleRegion();

        app.top = vr.latLngBounds.northeast.latitude;
        app.bottom = vr.latLngBounds.southwest.latitude;
        app.left = vr.latLngBounds.southwest.longitude;
        app.right = vr.latLngBounds.northeast.longitude;

        Log.e(TAG, "TOP :" + app.top + " RIGHT :" + app.right);
        Log.e(TAG, "BOTTOM : " + app.bottom + " LEFT :" + app.left);
        if (TLVIEW_MAP_MOVE == true) {
            TLVIEW_MAP_MOVE = false;
        } else {
            //prg.show();
            reloadPhotoBucketMap();
        }
    }

    /**
     * bn
     * ImageButton Click Listeners
     */
    @OnClick(R.id.header_right_button)
    public void onAlbumDetailClick() {
        mClusterManager.clearItems();
        if (MAP_VISIBLE_MODE == false) {
            if (map_flag == 0) {
                mMapContainer.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(R.drawable.play);
                mBackButton.setImageResource(R.drawable.back);
                MAP_VISIBLE_MODE = true;
            } else {
                mPhotoBucketFrame.setVisibility(View.VISIBLE);
                mMapContainer.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(R.drawable.play);
                mBackButton.setImageResource(R.drawable.back);
                MAP_VISIBLE_MODE = true;
            }
        } else {
            //PhotoBucketFragment bucketFragment = (PhotoBucketFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Global.FRAGMENT_PHOTO_BUCKET);
            photoArray = cursorFrag.getCurrentBucket();
            Intent intent = new Intent(getActivity(), SliderActivity.class);
            intent.putExtra("com.kairos.photrace.photoArray", photoArray);
            getActivity().startActivity(intent);
        }
    }

    @OnClick(R.id.header_left_button)
    public void onBackButtonlClick() {
        if (MAP_VISIBLE_MODE == true) {
            if (map_flag == 0) {
                mClusterManager.clearItems();
                mMapContainer.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params1.height = bucket_size;
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(R.drawable.result);
                mBackButton.setImageResource(0);
                MAP_VISIBLE_MODE = false;
            } else {
                mMapContainer.setVisibility(View.VISIBLE);
                params_container.height = map_container_size + bucket_size;
                mMapContainer.setLayoutParams(params_container);
                params.height = map_size + bucket_size;
                mMapFragment.getView().setLayoutParams(params);
                mPhotoBucketFrame.setVisibility(View.GONE);
                mResultButton.setImageResource(R.drawable.result);
                mBackButton.setImageResource(0);
                MAP_VISIBLE_MODE = false;
            }
        }

    }

    @OnClick(R.id.map_button)
    public void onMapButtonClick() {
        if (map_flag == 0) {
            mMapButton.setImageResource(R.drawable.minus);
            mResultButton.setImageResource(0);

            params_tlview_container.height = tlview_container_size;
            mTLviewContainer.setLayoutParams(params_tlview_container);
            params_container.height = map_container_size + bucket_size;
            mMapContainer.setLayoutParams(params_container);
            params.height = map_size + bucket_size;
            mMapFragment.getView().setLayoutParams(params);

            mPhotoBucketFrame.setVisibility(View.GONE);
            mMap.clear();
            mResultButton.setImageResource(R.drawable.result);
            mClusterManager.clearItems();

           // startDemo();
            prg.show();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    startDemo();
                    reloadPhotoBucketMap();
                    prg.dismiss();
                }
            };
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 1000);
            //if (mLocationphotoArray.size() > 0) {
            //}
            //getRegion();
            map_flag++;
        } else {

            params_container.height = map_container_size;
            mMapContainer.setLayoutParams(params_container);
            params.height = map_size;
            mMapFragment.getView().setLayoutParams(params);

            params_photo_bucket_container.height = bucket_size;
            mPhotoBucketFrame.setLayoutParams(params_photo_bucket_container);
            mPhotoBucketFrame.setVisibility(View.VISIBLE);
            mMapButton.setImageResource(R.drawable.plus);
            mMap.clear();
            mClusterManager.clearItems();
            mPhotoMarkerClusterManager.clearItems();
            getRegion();
            map_flag--;
        }

    }

   //new
    @Subscribe
    public void onClusteringEvent(onClusteringEvent event) {
        if (map_flag == 0) { //small map
            mMap.clear();
            mClusterManager.clearItems();
            mClusterManager.addItems(event.getPhotos());
            mClusterManager.cluster();
            if (event.getBounds() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(event.getBounds(), 70));
                TLVIEW_MAP_MOVE = true;
            }
        } else { // big map
            mMap.clear();
            mPhotos = event.getPhotoMarkers();
            startDemo();
            if (event.getBounds() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(event.getBounds(), 70));
                TLVIEW_MAP_MOVE = true;
            }
        }

        showTimerangeAndSize();
    }

    @Subscribe
    public void onClusteringEventForMap(onClusteringEventForMap event) {
        if (map_flag == 0) {
            mClusterManager.clearItems();
            mClusterManager.addItems(event.getPhotos());
            mClusterManager.cluster();

            timeSliderFlag = true;
            if(app.photoTotalNum > 0) {
                timeSliderView.invalidate();
                Log.e("Num3","" + app.photoTotalNum);
            }


        } else {
            if (app.photoTotalNum > 0) {
                if (app.photoTotalNum > 0) {
                    mPhotoMarkerClusterManager.clearItems();
                    mPhotos = event.getPhotoMarkers();
                    startDemo();
                    timeSliderFlag = true;
                    if(app.photoTotalNum > 0) {
                        timeSliderView.invalidate();
                    }
                }
            } else {
                mMap.clear();
            }
        }
        Log.e("Num4","" + app.photoTotalNum);
        showTimerangeAndSize();

    }

    @Subscribe
    public void onActionUP(Action_UpEvent event) {
        if (event != null) {
            TLVIEW_CLICK_MODE = true;
            setTimebarRange(event.getLeftMillis(), event.getRightMillis());
            app.leftInMillis = event.getLeftMillis();
            app.rightInMillis = event.getRightMillis();
            reloadPhotoBucket();
        }
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PhotoMarkerRenderer extends DefaultClusterRenderer<PhotoMarker> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoMarkerRenderer() {
            super(getActivity().getApplicationContext(), mMap, mPhotoMarkerClusterManager);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);

            mClusterIconGenerator.setContentView(multiProfile);
            mIconGenerator.setColor(Color.argb(255, 224, 83, 83));
            mClusterIconGenerator.setColor(Color.argb(255, 224, 83, 83));
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            mIconGenerator.setContentView(mImageView);
        }


        @Override
        protected void onBeforeClusterItemRendered(PhotoMarker photo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageBitmap(MediaUtil.getThumbnail(photo.photoId));
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<PhotoMarker> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;
            for (PhotoMarker p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = new BitmapDrawable(getResources(), MediaUtil.getThumbnail(p.photoId));
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);
            mClusterImageView.setImageDrawable(multiDrawable);

            Log.e("", "cluster size; " + cluster.getSize());
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<PhotoMarker> cluster) {
        int id = cluster.getItems().iterator().next().photoId;
        Intent intent = new Intent(getActivity(), MarkerPhotoDetailActivity2.class);
        intent.putExtra("com.kairos.photrace.photoposition", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        // Show a toast with some info when the cluster is clicked.
        Toast.makeText(getActivity(), "" + cluster.getSize(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PhotoMarker> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(PhotoMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
        int id = item.photoId;
        Intent intent = new Intent(getActivity(), MarkerPhotoDetailActivity2.class);
        intent.putExtra("com.kairos.photrace.photoposition", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PhotoMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    protected void startDemo() {
        mMap.clear();
        mPhotoMarkerClusterManager.clearItems();
        mPhotoMarkerClusterManager.setRenderer(new PhotoMarkerRenderer());
        mMap.setOnInfoWindowClickListener(mPhotoMarkerClusterManager);
        mPhotoMarkerClusterManager.setOnClusterItemClickListener(this);
        mPhotoMarkerClusterManager.setOnClusterClickListener(this);
        mPhotoMarkerClusterManager.setOnClusterInfoWindowClickListener(this);
        mPhotoMarkerClusterManager.setOnClusterItemClickListener(this);
        mPhotoMarkerClusterManager.setOnClusterItemInfoWindowClickListener(this);
        addItems();

    }

    private void addItems() {
        mPhotoMarkerClusterManager.addItems(mPhotos);
        mPhotoMarkerClusterManager.cluster();

    }


}

