/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kairos.photoremember.R;
import com.kairos.photoremember.database.DataBaseHelper;
import com.kairos.photoremember.util.BusProvider;

public class SplashActivity extends Activity {

    private DataBaseHelper mDbHelper;

    private final Runnable nextActivityLauncher = new Runnable() {
        @Override
        public void run() {
            startNextActivity();
        }
    };

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        BusProvider.getBus().register(this);
        mDbHelper = DataBaseHelper.getInstance();
        mDbHelper.initDataBase();
       // MediaSyncTask task = new MediaSyncTask();
       // task.execute();
        handler.postDelayed(nextActivityLauncher, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }

    protected void startNextActivity() {
        /**
         * Add Time Bomb at this point if needed
         */

        Intent intent = new Intent(this, PhoTraceActivity.class);
        startActivity(intent);
        finish();
    }


}
