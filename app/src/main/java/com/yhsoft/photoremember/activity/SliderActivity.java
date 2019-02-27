package com.yhsoft.photoremember.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.yhsoft.photoremember.R;
import com.yhsoft.photoremember.util.MediaUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.OnClick;

public class SliderActivity extends AppCompatActivity {
    ImageButton mBackButton;
	
	public int currentimageindex = 0;
	ImageView slidingimage;
    ArrayList<Integer> photoArray;
    private Handler mHandler;
    private Timer timer;
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_slider);
        slidingimage = (ImageView)findViewById(R.id.ImageView3_Left);
        slidingimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Log.e("", "back click !");
            }
        });
        mBackButton = (ImageButton)findViewById(R.id.header_left_button);
        mBackButton.setImageResource(R.drawable.back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Log.e("", "back click !");
            }
        });
        photoArray = getIntent().getExtras().getIntegerArrayList("com.yhsoft.photrace.photoArray");
        mHandler = new Handler();

        // Create runnable for posting
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
            	AnimateandSlideShow();
            }
        };

        int delay = 0; // delay for 1 sec.

        int period = 2000; // repeat every 4 sec.

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
        public void run() {
        	 mHandler.post(mUpdateResults);
        }
        }, delay, period);
		 
		       
    }

    public void onClick(View v) {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
      }
    
    /**
     * Helper method to start the animation on the splash screen
     */
    private void AnimateandSlideShow() {
        Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.anim_slide_from_right);
        if(currentimageindex < photoArray.size()) {
           // slidingimage.setImageBitmap(MediaUtil.getImageBitmap(photoArray.get(currentimageindex)));
            MediaUtil.setFitImage(slidingimage, photoArray.get(currentimageindex));
        //slidingimage.startAnimation(rotateimage);
            currentimageindex++;
        }else{
            timer.cancel();
        }
    }

    @OnClick(R.id.header_left_button)
    public void onBackButtonClick() {
        onBackPressed();
        Log.e("", "back click !");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();

    }
    
    
}