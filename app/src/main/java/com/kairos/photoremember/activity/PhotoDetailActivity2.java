package com.kairos.photoremember.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kairos.photoremember.PhoTrace;
import com.kairos.photoremember.R;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.MediaUtil;
import com.kairos.photoremember.view.TouchImageView;
import com.squareup.picasso.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * It ues low quality photo (big thumbnail), but faster than PhotoDetailActivity
 * It can pinch zoom in/out image, and swipe photo right to left(or left to right)
 * PhotoDetailActivity can't pinch zoom in/out image,
 * from bella
 */
public class PhotoDetailActivity2 extends Activity {

    ArrayList<Integer> photoArray;
    ArrayList<Integer> allphotoArray;

    int a = 0;
    int swipePhotoIndex = 0;
    int swipePhotoId = 0;
    TouchImageView imgPhotoDetail;
    private ProgressDialog mProg;

    private PopupWindow mPopupWindow;
    private TextView date;
    private TextView address;
    private ImageView cancel_button;

    private GestureDetector gd = null;
    private final static int SCALED_TOUCH_SLOP = ViewConfiguration.get(PhoTrace.getContext()).getScaledTouchSlop();

    Context mContext;
    PhoTrace app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_detail2);
        imgPhotoDetail = (TouchImageView) findViewById(R.id.image);
        gd = new GestureDetector(this, new SwipeGestureDetector());
        cancel_button = (ImageView) findViewById(R.id.cancel_button);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.photo_detail_info, null);
                mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setAnimationStyle(-1);
                date = (TextView) popupView.findViewById(R.id.photo_date);
                address = (TextView) popupView.findViewById(R.id.photo_address);
                mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 100);
                setPhotoDate(swipePhotoIndex, date, address);
                //MediaUtil.setFitTouchImage(imgPhotoDetail, a);
               // MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, a, callback);
            }
        }, 100L);

        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();

        if (getIntent().getExtras() != null) {
            a = getIntent().getExtras().getInt("com.kairos.photrace.position");

            //MediaUtil.setScaledImage2(imgPhotoDetail, a);
           //MediaUtil.setFitTouchImage(imgPhotoDetail, a);
            //MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, a, callback);
            imgPhotoDetail.setImageBitmap(MediaUtil.getImageBitmap(a));
            swipePhotoId = a;
            for (int i = 0; i < app.photoArray.size(); i++) {
                if (app.photoArray.get(i) == swipePhotoId) {
                    swipePhotoIndex = i;
                }
            }

        }

        imgPhotoDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean ret = true;
                ret = gd.onTouchEvent(motionEvent) || ret;
                return true;
            }
        });



        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Log.e("", "back click !");
            }
        });
    }

    Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            //mProg.dismiss();
        }

        @Override
        public void onError() {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    private void setPhotoDate(int swipePhotoIndex, TextView setdate, TextView setaddress) {
        boolean bCheckEdit = false;
        Geocoder gc = new Geocoder(this, Locale.US);
        String addressString = "no Address found";
        try {
            List<Address> addresses = gc.getFromLocation(app.latArray.get(swipePhotoIndex), app.lngArray.get(swipePhotoIndex), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");
                sb.append(address.getCountryName()).append(" ");//nation
                sb.append(address.getLocality()).append(" ");//city
                addressString = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setdate.setText(DateUtil.getDateString(app.dateArray.get(swipePhotoIndex), DateUtil.RANGE_DAY));
        setaddress.setText(addressString);
    }

    public final class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /**
             * Insure the minimum distance
             */
            if (Math.abs(e1.getRawX() - e2.getRawX()) > SCALED_TOUCH_SLOP) {
                if (e2.getRawX() > e1.getRawX()) {


                    if (swipePhotoIndex > 0) {
                        swipePhotoIndex--;
                    }
                    //mProg.show();
                    imgPhotoDetail.setImageBitmap(MediaUtil.getImageBitmap(app.photoArray.get(swipePhotoIndex)));
                    //MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, app.photoArray.get(swipePhotoIndex), callback);
                    setPhotoDate(swipePhotoIndex, date, address);

                } else if (e2.getRawX() < e1.getRawX()) {
                    if (app.photoArray.size() - 1 > swipePhotoIndex) {
                        swipePhotoIndex++;
                    }
                    //mProg.show();
                    imgPhotoDetail.setImageBitmap(MediaUtil.getImageBitmap(app.photoArray.get(swipePhotoIndex)));
                    //MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, app.photoArray.get(swipePhotoIndex), callback);
                    setPhotoDate(swipePhotoIndex, date, address);
                }
            }
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

}
