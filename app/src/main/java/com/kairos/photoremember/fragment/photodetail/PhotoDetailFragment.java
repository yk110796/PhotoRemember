/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.fragment.photodetail;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kairos.photoremember.R;
import com.kairos.photoremember.database.DataBaseHelper;
import com.kairos.photoremember.database.model.Photo;
import com.kairos.photoremember.event.HeaderActionEvent;
import com.kairos.photoremember.util.BusProvider;
import com.kairos.photoremember.util.DateUtil;
import com.kairos.photoremember.util.DebugLog;
import com.kairos.photoremember.util.MediaUtil;
import com.kairos.photoremember.view.ResizeImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Common Filter Fragment for My Time Trace fragment_my
 * This fragment_my selects the user preferred decision
 * and notifies to main fragment_my(MyTimeTrace) with result via event
 */
public class PhotoDetailFragment extends Fragment {
    private static Fragment mInstance;
    private static int mFocusedIndex = -1;
    private PopupWindow mPopupWindow;
    private int flag = 0;
    private TextView date;
    private TextView address;
    public static Photo mCurrentPhoto = null;

    @InjectView(R.id.header_left_button)
    ImageButton mLeftButton;
    @InjectView(R.id.global_title_text)
    TextView mTitle;
    @InjectView(R.id.display_image)
    ResizeImageView mImageView;



//    @InjectView(R.id.photo_title)
//    TextView mPhotoTitle;
//    @InjectView(R.id.photo_date)
//    TextView mPhotoDate;



            public static ArrayList<Integer> mList = new ArrayList<>();

        public static Fragment getInstance(int index, ArrayList<Integer> list) {
            if (mInstance == null) {
            mInstance = new PhotoDetailFragment();
        }
        mList.clear();
        mCurrentPhoto = null;
        mFocusedIndex = index;
        mList = list;
        DebugLog.e("PhotoDetail Get Instance");
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ButterKnife.inject(this, rootView);

       // mLeftButton.setImageResource(R.drawable.ic_action_back_simple);
        MediaUtil.setFitImage(mImageView, mList.get(mFocusedIndex));
        //setPhotoInformation();

        mImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.photo_detail_info, null);
                if(flag == 0) {
                    mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setAnimationStyle(-1);
                    mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 100);
                    date = (TextView) popupView.findViewById(R.id.photo_date);
                    address = (TextView) popupView.findViewById(R.id.photo_address);

                    setPhotoDate(date, address);
                   // setPhotoInformation();
                    //date.setText("dfghjkl");
                    flag++;
                }else{
                    mPopupWindow.dismiss();
                    flag--;
                }
                //setPhotoInformation();
            }
        });
        return rootView;
    }
    private void setPhotoDate(TextView setdate, TextView setaddress) {
        boolean bCheckEdit = false;
        mCurrentPhoto = DataBaseHelper.getInstance().selectPhoto(mList.get(mFocusedIndex));
        Geocoder gc = new Geocoder(getActivity(), Locale.KOREAN);
        String addressString = "no Address found";
        try {
            List<Address> addresses = gc.getFromLocation(mCurrentPhoto.getLatitude(), mCurrentPhoto.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    sb.append(address.getAddressLine(i)).append("\n");
                sb.append(address.getCountryName()).append(" ");//nation
                sb.append(address.getLocality()).append(" ");//city
                //sb.append(address.getSubLocality() + " ");//
                sb.append(address.getThoroughfare()).append(" ");
                sb.append(address.getFeatureName()).append(" ");
                addressString = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mCurrentPhoto != null) {
            DebugLog.e("Photo Info Exist");
            if (mCurrentPhoto.getTitle() != null) {
                DebugLog.e("Photo Title Exist");
                if (mCurrentPhoto.getTitle().length() == 0) {
                    if (mCurrentPhoto.getDate() > 0) {
                        DebugLog.e("Title Exist but length is zero --> Set date");
                        setdate.setText(DateUtil.getDateString(mCurrentPhoto.getDate(), DateUtil.RANGE_DAY));
                        setaddress.setText(addressString);
                        } else {
                        DebugLog.e("Title Exist but length/date is zero --> set title");
                        setdate.setText(R.string.photo_detail_title);
                        setaddress.setText(addressString);
                    }
                    setdate.setText(R.string.photo_detail_title);
                } else {
                    DebugLog.e("Set Photo Title");
                    date.setText(mCurrentPhoto.getTitle());
                    address.setText(addressString);
                }
            } else {
                DebugLog.e("Photo Title is NULL");
                setdate.setText(DateUtil.getDateString(mCurrentPhoto.getDate(), DateUtil.RANGE_DAY));
                setaddress.setText(addressString);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mInstance = null;
    }

    @OnClick(R.id.header_left_button)
    public void onBackClick() {
        BusProvider.getBus().post(new HeaderActionEvent(HeaderActionEvent.SelectedAction.ACTION_BACK));
    }


}