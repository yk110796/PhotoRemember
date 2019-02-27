package com.yhsoft.photoremember.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.squareup.picasso.Callback;
import com.yhsoft.photoremember.PhoTrace;
import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.activity.util.SystemUiHider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.MediaUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MarkerPhotoDetailActivity extends AppCompatActivity {

    private ImageView imgPhotoDetail;
    private ProgressDialog mProg;

    int a = 0;
    int swipePhotoIndex = 0;
    int swipePhotoId = 0;

    private GestureDetector gd = null;
    private final static int SCALED_TOUCH_SLOP = ViewConfiguration.get(PhoTrace.getContext()).getScaledTouchSlop();

    private PopupWindow mPopupWindow;
    private int flag = 0;
    private TextView date;
    private TextView address;
    private ImageView cancel_button;
    Context mContext;
    PhoTrace app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_detail);
        imgPhotoDetail = (ImageView) findViewById(R.id.img_photodetail);
        mProg = new ProgressDialog(this);
        mProg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProg.setMessage("loading..");
        mProg.show();
        gd = new GestureDetector(this, new SwipeGestureDetector());
        cancel_button = (ImageView) findViewById(R.id.cancel_button);

        mContext = PhoTrace.getContext();
        app = (PhoTrace) mContext.getApplicationContext();

        if (getIntent().getExtras() != null) {
            a = getIntent().getExtras().getInt("com.yhsoft.photrace.photoposition");
            MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, a, callback);
            swipePhotoId = a;
            for (int i = 0; i < app.photoArray.size(); i++) {
                Log.e("", "a =" + app.photoArray.get(i));
                if (app.photoArray.get(i) == swipePhotoId) {
                    swipePhotoIndex = i;
                }
            }

        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.photo_detail_info, null);
                mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setAnimationStyle(-1);
                date = (TextView) popupView.findViewById(R.id.photo_date);
                address = (TextView) popupView.findViewById(R.id.photo_address);
                mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 100);
                //setPhotoDate(swipePhotoIndex, date, address);
                Log.e("232", "" + app.lngArray.size());
                setPhotoDate(swipePhotoIndex, date, address);
            }
        }, 100L);

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
            mProg.dismiss();
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

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    public final class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /**
             * Insure the minimum distance
             */
            mProg.show();
            if (Math.abs(e1.getRawX() - e2.getRawX()) > SCALED_TOUCH_SLOP) {
                if (e2.getRawX() > e1.getRawX()) {
                    if (swipePhotoIndex > 0) {
                        swipePhotoIndex--;
                    }
                    MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, app.photoArray.get(swipePhotoIndex), callback);
                    setPhotoDate(swipePhotoIndex, date, address);
                    Log.e("", "left to right swipe");

                } else if (e2.getRawX() < e1.getRawX()) {
                    if (app.photoArray.size() - 1 > swipePhotoIndex) {
                        swipePhotoIndex++;
                    }
                    MediaUtil.setFitImageWithCompletedCallBack(imgPhotoDetail, app.photoArray.get(swipePhotoIndex), callback);
                    setPhotoDate(swipePhotoIndex, date, address);
                    Log.e("", "right to left swipe");
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
