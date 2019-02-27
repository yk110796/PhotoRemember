package com.yhsoft.photoremember.fragment.photodetail;

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

import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.database.DataBaseHelper;
import com.yhsoft.photoremember.database.model.Photo;
import com.yhsoft.photoremember.event.HeaderActionEvent;
import com.yhsoft.photoremember.util.BusProvider;
import com.yhsoft.photoremember.util.DateUtil;
import com.yhsoft.photoremember.util.DebugLog;
import com.yhsoft.photoremember.util.MediaUtil;
import com.yhsoft.photoremember.view.ResizeImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.Gravity.BOTTOM;
import static android.view.View.OnClickListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.LayoutParams;
import static butterknife.ButterKnife.inject;
import static com.yhsoft.photoremember.R.id;
import static com.yhsoft.photoremember.R.id.display_image;
import static com.yhsoft.photoremember.R.id.global_title_text;
import static com.yhsoft.photoremember.R.id.header_left_button;
import static com.yhsoft.photoremember.R.id.photo_address;
import static com.yhsoft.photoremember.R.id.photo_date;
import static com.yhsoft.photoremember.R.layout;
import static com.yhsoft.photoremember.R.layout.fragment_photo_detail;
import static com.yhsoft.photoremember.R.layout.photo_detail_info;
import static com.yhsoft.photoremember.R.string;
import static com.yhsoft.photoremember.R.string.photo_detail_title;
import static com.yhsoft.photoremember.event.HeaderActionEvent.SelectedAction;
import static com.yhsoft.photoremember.event.HeaderActionEvent.SelectedAction.ACTION_BACK;
import static com.yhsoft.photoremember.util.BusProvider.getBus;
import static com.yhsoft.photoremember.util.DateUtil.RANGE_DAY;
import static com.yhsoft.photoremember.util.DateUtil.getDateString;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static com.yhsoft.photoremember.util.MediaUtil.setFitImage;
import static java.util.Locale.KOREAN;

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

    @InjectView(header_left_button)
    ImageButton mLeftButton;
    @InjectView(global_title_text)
    TextView mTitle;
    @InjectView(display_image)
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
        e("PhotoDetail Get Instance");
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(fragment_photo_detail, container, false);
        inject(this, rootView);

        // mLeftButton.setImageResource(R.drawable.ic_action_back_simple);
        setFitImage(mImageView, mList.get(mFocusedIndex));
        //setPhotoInformation();

        mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // your code here
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(photo_detail_info, null);
                if (flag == 0) {
                    mPopupWindow = new PopupWindow(popupView, MATCH_PARENT, WRAP_CONTENT);
                    mPopupWindow.setAnimationStyle(-1);
                    mPopupWindow.showAtLocation(popupView, BOTTOM, 0, 100);
                    date = (TextView) popupView.findViewById(photo_date);
                    address = (TextView) popupView.findViewById(photo_address);

                    setPhotoDate(date, address);
                    // setPhotoInformation();
                    //date.setText("dfghjkl");
                    flag++;
                } else {
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
        Geocoder gc = new Geocoder(getActivity(), KOREAN);
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
            e("Photo Info Exist");
            if (mCurrentPhoto.getTitle() != null) {
                e("Photo Title Exist");
                if (mCurrentPhoto.getTitle().length() == 0) {
                    if (mCurrentPhoto.getDate() > 0) {
                        e("Title Exist but length is zero --> Set date");
                        setdate.setText(getDateString(mCurrentPhoto.getDate(), RANGE_DAY));
                        setaddress.setText(addressString);
                    } else {
                        e("Title Exist but length/date is zero --> set title");
                        setdate.setText(photo_detail_title);
                        setaddress.setText(addressString);
                    }
                    setdate.setText(photo_detail_title);
                } else {
                    e("Set Photo Title");
                    date.setText(mCurrentPhoto.getTitle());
                    address.setText(addressString);
                }
            } else {
                e("Photo Title is NULL");
                setdate.setText(getDateString(mCurrentPhoto.getDate(), RANGE_DAY));
                setaddress.setText(addressString);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mInstance = null;
    }

    @OnClick(header_left_button)
    public void onBackClick() {
        getBus().post(new HeaderActionEvent(ACTION_BACK));
    }


}