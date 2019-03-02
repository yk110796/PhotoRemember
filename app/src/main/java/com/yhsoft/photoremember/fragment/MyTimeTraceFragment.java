package com.yhsoft.photoremember.fragment;

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
import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.activity.MarkerPhotoDetailActivity2;
import com.yhsoft.photoremember.activity.SliderActivity;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.database.model.PhotoMarker;
import com.yhsoft.photoremember.event.Action_UpEvent;
import com.yhsoft.photoremember.event.onClusteringEvent;
import com.yhsoft.photoremember.event.onClusteringEventForMap;
import com.yhsoft.photoremember.fragment.map.MultiDrawable;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.MediaUtil;
import com.yhsoft.photoremember.util.Preference;
import com.yhsoft.photoremember.view.TLView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.app.ProgressDialog.STYLE_SPINNER;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.Color.argb;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.ViewConfiguration.get;
import static android.view.ViewConfiguration.getLongPressTimeout;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static butterknife.ButterKnife.inject;
import static butterknife.ButterKnife.reset;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;
import static com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;
import static com.yhsoft.photoremember.PhoTrace.getContext;
import static com.yhsoft.photoremember.R.dimen;
import static com.yhsoft.photoremember.R.dimen.custom_profile_image;
import static com.yhsoft.photoremember.R.drawable;
import static com.yhsoft.photoremember.R.drawable.back;
import static com.yhsoft.photoremember.R.drawable.bubble5;
import static com.yhsoft.photoremember.R.drawable.bubble6;
import static com.yhsoft.photoremember.R.drawable.logo;
import static com.yhsoft.photoremember.R.drawable.minus;
import static com.yhsoft.photoremember.R.drawable.play;
import static com.yhsoft.photoremember.R.drawable.plus;
import static com.yhsoft.photoremember.R.drawable.result;
import static com.yhsoft.photoremember.R.id;
import static com.yhsoft.photoremember.R.id.bubble_imageView;
import static com.yhsoft.photoremember.R.id.bucket_container;
import static com.yhsoft.photoremember.R.id.header_center_button;
import static com.yhsoft.photoremember.R.id.header_left_button;
import static com.yhsoft.photoremember.R.id.header_right_button;
import static com.yhsoft.photoremember.R.id.image;
import static com.yhsoft.photoremember.R.id.map_button;
import static com.yhsoft.photoremember.R.id.my_map;
import static com.yhsoft.photoremember.R.id.my_map_container;
import static com.yhsoft.photoremember.R.id.timeslider_text;
import static com.yhsoft.photoremember.R.id.tl_content;
import static com.yhsoft.photoremember.R.id.tl_overlay;
import static com.yhsoft.photoremember.R.id.tlview_container;
import static com.yhsoft.photoremember.R.layout;
import static com.yhsoft.photoremember.R.layout.bubble_activity;
import static com.yhsoft.photoremember.R.layout.fragment_mytimetrace;
import static com.yhsoft.photoremember.R.layout.multi_profile;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_DAY;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_YEAR;
import static com.yhsoft.photoremember.util.DateUtil.getDateString;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.MediaUtil.getThumbnail;
import static com.yhsoft.photoremember.util.Preference.KEY_APP_FIRST_START;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_LEFT;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_RIGHT;
import static com.yhsoft.photoremember.util.Preference.KEY_MAP_VALUE_TOP;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_BUCKET_START_MODE;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_DATE_PICK_MODE;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_PHOTO_RANGE_TOP;
import static com.yhsoft.photoremember.util.Preference.KEY_TIMEBAR_RANGE_BOTTOM;
import static com.yhsoft.photoremember.util.Preference.KEY_TIMEBAR_RANGE_TOP;
import static com.yhsoft.photoremember.util.Preference.getInt;
import static com.yhsoft.photoremember.util.Preference.getLong;
import static com.yhsoft.photoremember.util.Preference.putBoolean;
import static com.yhsoft.photoremember.util.Preference.putInt;
import static com.yhsoft.photoremember.util.Preference.putLong;
import static java.lang.Long.MIN_VALUE;
import static java.lang.Math.min;
import static java.lang.String.valueOf;

public class MyTimeTraceFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<PhotoMarker>,
        ClusterManager.OnClusterInfoWindowClickListener<PhotoMarker>,
        ClusterManager.OnClusterItemClickListener<PhotoMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PhotoMarker> {

    @InjectView(header_center_button)
    ImageButton mCenter;
    @InjectView(header_right_button)
    ImageButton mResultButton;
    @InjectView(header_left_button)
    ImageButton mBackButton;
    @InjectView(my_map_container)
    LinearLayout mMapContainer;

    @InjectView(bucket_container)
    FrameLayout mPhotoBucketFrame;

    @InjectView(map_button)
    ImageView mMapButton;
    @InjectView(tl_overlay)
    FrameLayout mTLviewOverlay;
    @InjectView(tlview_container)
    FrameLayout mTLviewContainer;

    @InjectView(timeslider_text)
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
    private final static int TIME_CHANGE_INTERVAL = getLongPressTimeout();
    //private final  int SCALED_TOUCH_SLOP = get(getContext()).getScaledTouchSlop();

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
    LayoutParams params;
    LayoutParams params_container;
    LayoutParams params_photo_bucket_container;
    LayoutParams params_tlview_container;
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
    private int[] bubbleImageId = {bubble5, bubble6};
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
        mContext = getContext();
        app = (PhoTrace) mContext.getApplicationContext();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reset(this);

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
        getBus().unregister(this);
        timeLinehandler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBus().register(this);
        runnable.run();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(fragment_mytimetrace, container, false);

        timeSliderView = (TLView) rootView.findViewById(tl_content);
        inject(this, rootView);

        m_vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        mCenter.setImageResource(logo);
        mResultButton.setImageResource(result);
        mMapButton.setImageResource(plus);
        mTLviewOverlay.setVisibility(VISIBLE);

        prg = new ProgressDialog(getActivity());
        prg.setMessage("Loading your picture...");
        prg.setProgressStyle(STYLE_SPINNER);
        prg.setCancelable(false);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(my_map);
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
                e("map_container :" + mMapContainer.getHeight());
                map_container_size = mMapContainer.getHeight();
            }
        });

        mPhotoBucketFrame.post(new Runnable() {
            @Override
            public void run() {
                e("map_container :" + mPhotoBucketFrame.getHeight());
                bucket_size = mPhotoBucketFrame.getHeight();
            }
        });

        mTLviewContainer.post(new Runnable() {
            @Override
            public void run() {
                e("map_container :" + mTLviewContainer.getHeight());
                tlview_container_size = mTLviewContainer.getHeight();
                app.tlview_height = tlview_container_size;
            }
        });

        mMapFragment.getView().post(new Runnable() {
            @Override
            public void run() {
                e("map_container :" + mMapFragment.getView().getHeight());
                map_size = mMapFragment.getView().getHeight();
            }
        });


        putLong(getContext(), KEY_MAP_VALUE_LEFT, mapSizeInitLeft);
        putLong(getContext(), KEY_MAP_VALUE_RIGHT, mapSizeInitRight);
        putLong(getContext(), KEY_MAP_VALUE_TOP, mapSizeInitTop);
        putLong(getContext(), KEY_MAP_VALUE_BOTTOM, mapSizeInitBottom);

        photoArray.clear();
        initializeRange();

        cursorFrag = (PhotoViewFragment) instantiate(getActivity(), PhotoViewFragment.class.getName());
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(bucket_container, cursorFrag, "main_fragment").commit();


        int firstAppStart = getInt(getActivity(), KEY_APP_FIRST_START);
        if (firstAppStart == 0) {
            showActivityOverlay();
        } else {

        }

        return rootView;
    }

    private void showActivityOverlay() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(bubble_activity, null);
                mPopupWindow = new PopupWindow(popupView, MATCH_PARENT, MATCH_PARENT, true);
                mPopupWindow.setAnimationStyle(-1);
//                mPopupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, 0);
                mBubbleImageView = (ImageView) popupView.findViewById(bubble_imageView);
                mBubbleImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (bubbleImageId.length > bubbleId) {
                            mBubbleImageView.setImageResource(bubbleImageId[bubbleId]);
                            bubbleId++;
                        } else {
                            putInt(getActivity(), KEY_APP_FIRST_START, NOTFIRST);
                            mPopupWindow.dismiss();
                        }
                    }
                });
            }
        }, 100L);
    }

    private void initializeRange() {
        long dateMin = getLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM);
        long dateMax = getLong(getContext(), KEY_TIMEBAR_RANGE_TOP);

        app.leftInMillis = getLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM);
        app.rightInMillis = getLong(getContext(), KEY_TIMEBAR_RANGE_TOP);
        if (dateMin == MIN_VALUE || dateMax == MIN_VALUE) {
            dateMin = getLong(getContext(), KEY_PHOTO_RANGE_BOTTOM);
            dateMax = getLong(getContext(), KEY_PHOTO_RANGE_TOP);
            app.leftInMillis = getLong(getContext(), KEY_PHOTO_RANGE_BOTTOM);
            app.rightInMillis = getLong(getContext(), KEY_PHOTO_RANGE_TOP);
            setTimebarRange(dateMin, dateMax);
        }

        int mode = getInt(getContext(), KEY_PHOTO_BUCKET_START_MODE);

        e("bottom string : " + getDateString(dateMin, RANGE_DAY));
        e("top string : " + getDateString(dateMax, RANGE_DAY));


        /**
         * Always start with normal mode
         */
        putBoolean(getContext(), KEY_PHOTO_DATE_PICK_MODE, false);
        bIsPickerMode = false;
    }

    //시간바의 날짜를 세팅한다
    private void setTimebarRange(long bottom, long top) {
        if (bottom != MIN_VALUE) {
            putLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM, bottom);
            //app.leftInMillis = bottom;
        }
        if (top != MIN_VALUE) {
            putLong(getContext(), KEY_TIMEBAR_RANGE_TOP, top);
        }
        mTimebarText.setText(getDateString(bottom, RANGE_DAY)
                + " - " + getDateString(top, RANGE_DAY) + "(" + app.photoTotalNum + ")");
        Log.e("Num1", "" + app.photoTotalNum);
    }


    private void setTimebarMode(int direction) {
        boolean dirtyFlag = false;
        int mode = getInt(getContext(), KEY_PHOTO_BUCKET_START_MODE);

        switch (direction) {
            case TIME_RANGE_TO_YEAR:    // Left side button long press
                if (mode > RANGE_YEAR) {
                    --mode;
                    dirtyFlag = true;
                }
                break;
            case TIME_RANGE_TO_DAY:     // Right side button long press
                if (mode < RANGE_DAY) {
                    ++mode;
                    dirtyFlag = true;
                }
                break;
            default:
                e("Abnormal Case");
                break;
        }

        if (dirtyFlag) {
            putInt(getContext(), KEY_PHOTO_BUCKET_START_MODE, mode);
            String bottom = getDateString(
                    getLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM),
                    mode);
            String top = getDateString(
                    getLong(getContext(), KEY_TIMEBAR_RANGE_TOP),
                    mode);
            //setTimebarText(bottom, top);

            reloadPhotoBucket();
        }
    }

    //***** 시간및 지역 변경후  다시 asynctask를 실행하는 중요 메서드
    private void reloadPhotoBucket() {
        if (cursorFrag != null)
            cursorFrag.reloadBucket();

    }

    private void reloadPhotoBucketMap() {
        if (cursorFrag != null)
            cursorFrag.reloadBucketForMap();


    }

    private void resetTimebarRange() {
        long dateMin = getLong(getContext(), KEY_PHOTO_RANGE_BOTTOM);
        long dateMax = getLong(getContext(), KEY_PHOTO_RANGE_TOP);
        setTimebarRange(dateMin, dateMax);
        refreshAllScreen();
    }

    private void refreshAllScreen() {
        String bottom = getDateString(
                getLong(getContext(), KEY_TIMEBAR_RANGE_BOTTOM),
                getInt(getContext(), KEY_PHOTO_BUCKET_START_MODE));
        String top = getDateString(
                getLong(getContext(), KEY_TIMEBAR_RANGE_TOP),
                getInt(getContext(), KEY_PHOTO_BUCKET_START_MODE));
        //setTimebarText(bottom, top);
        reloadPhotoBucket();
    }

    private void showTimerangeAndSize() {
        mTimebarText.setText(getDateString(app.leftInMillis, RANGE_DAY)
                + " - " + getDateString(app.rightInMillis, RANGE_DAY) + "(" + app.photoTotalNum + ")");
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
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
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
        e("map move");
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
    @OnClick(header_right_button)
    public void onAlbumDetailClick() {
        mClusterManager.clearItems();
        if (MAP_VISIBLE_MODE == false) {
            if (map_flag == 0) {
                mMapContainer.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(play);
                mBackButton.setImageResource(back);
                MAP_VISIBLE_MODE = true;
            } else {
                mPhotoBucketFrame.setVisibility(VISIBLE);
                mMapContainer.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(play);
                mBackButton.setImageResource(back);
                MAP_VISIBLE_MODE = true;
            }
        } else {
            //PhotoBucketFragment bucketFragment = (PhotoBucketFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Global.FRAGMENT_PHOTO_BUCKET);
            photoArray = cursorFrag.getCurrentBucket();
            Intent intent = new Intent(getActivity(), SliderActivity.class);
            intent.putExtra("com.yhsoft.photrace.photoArray", photoArray);
            getActivity().startActivity(intent);
        }
    }

    @OnClick(header_left_button)
    public void onBackButtonlClick() {
        if (MAP_VISIBLE_MODE == true) {
            if (map_flag == 0) {
                mClusterManager.clearItems();
                mMapContainer.setVisibility(VISIBLE);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                params1.height = bucket_size;
                mPhotoBucketFrame.setLayoutParams(params1);
                mResultButton.setImageResource(result);
                mBackButton.setImageResource(0);
                MAP_VISIBLE_MODE = false;
            } else {
                mMapContainer.setVisibility(VISIBLE);
                params_container.height = map_container_size + bucket_size;
                mMapContainer.setLayoutParams(params_container);
                params.height = map_size + bucket_size;
                mMapFragment.getView().setLayoutParams(params);
                mPhotoBucketFrame.setVisibility(GONE);
                mResultButton.setImageResource(result);
                mBackButton.setImageResource(0);
                MAP_VISIBLE_MODE = false;
            }
        }

    }

    @OnClick(map_button)
    public void onMapButtonClick() {
        if (map_flag == 0) {
            mMapButton.setImageResource(minus);
            mResultButton.setImageResource(0);

            params_tlview_container.height = tlview_container_size;
            mTLviewContainer.setLayoutParams(params_tlview_container);
            params_container.height = map_container_size + bucket_size;
            mMapContainer.setLayoutParams(params_container);
            params.height = map_size + bucket_size;
            mMapFragment.getView().setLayoutParams(params);

            mPhotoBucketFrame.setVisibility(GONE);
            mMap.clear();
            mResultButton.setImageResource(result);
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
            mPhotoBucketFrame.setVisibility(VISIBLE);
            mMapButton.setImageResource(plus);
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
             //   mMap.animateCamera(newLatLngBounds(event.getBounds(), 70));
                TLVIEW_MAP_MOVE = true;
            }
        } else { // big map
            mMap.clear();
            mPhotos = event.getPhotoMarkers();
            startDemo();
            if (event.getBounds() != null) {
           //     mMap.animateCamera(newLatLngBounds(event.getBounds(), 70));
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
            if (app.photoTotalNum > 0) {
                timeSliderView.invalidate();
                Log.e("Num3", "" + app.photoTotalNum);
            }


        } else {
            if (app.photoTotalNum > 0) {
                if (app.photoTotalNum > 0) {
                    mPhotoMarkerClusterManager.clearItems();
                    mPhotos = event.getPhotoMarkers();
                    startDemo();
                    timeSliderFlag = true;
                    if (app.photoTotalNum > 0) {
                        timeSliderView.invalidate();
                    }
                }
            } else {
                mMap.clear();
            }
        }
        Log.e("Num4", "" + app.photoTotalNum);
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

            View multiProfile = getActivity().getLayoutInflater().inflate(multi_profile, null);

            mClusterIconGenerator.setContentView(multiProfile);
            mIconGenerator.setColor(argb(255, 224, 83, 83));
            mClusterIconGenerator.setColor(argb(255, 224, 83, 83));
            mClusterImageView = (ImageView) multiProfile.findViewById(image);
            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int) getResources().getDimension(custom_profile_image);
            mImageView.setLayoutParams(new LayoutParams(mDimension, mDimension));
            mIconGenerator.setContentView(mImageView);
        }


        @Override
        protected void onBeforeClusterItemRendered(PhotoMarker photo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageBitmap(getThumbnail(photo.photoId));
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<PhotoMarker> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;
            for (PhotoMarker p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = new BitmapDrawable(getResources(), getThumbnail(p.photoId));
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);
            mClusterImageView.setImageDrawable(multiDrawable);

            Log.e("", "cluster size; " + cluster.getSize());
            Bitmap icon = mClusterIconGenerator.makeIcon(valueOf(cluster.getSize()));
            markerOptions.icon(fromBitmap(icon));
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
        intent.putExtra("com.yhsoft.photrace.photoposition", id);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        // Show a toast with some info when the cluster is clicked.
        makeText(getActivity(), "" + cluster.getSize(), LENGTH_SHORT).show();
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
        intent.putExtra("com.yhsoft.photrace.photoposition", id);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
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

