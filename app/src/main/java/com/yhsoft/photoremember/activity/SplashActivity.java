package com.yhsoft.photoremember.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yhsoft.photoremember.database.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.yhsoft.photoremember.R.layout.activity_splash;
import static com.yhsoft.photoremember.database.DataBaseHelper.getInstance;
import static com.yhsoft.photoremember.util.BusProvider.getBus;

public class SplashActivity extends AppCompatActivity {

    private DataBaseHelper mDbHelper;

    private final Runnable nextActivityLauncher = new Runnable() {
        @Override
        public void run() {
            startNextActivity();
        }
    };

    final Handler handler = new Handler();
    private String TAG = this.getClass().getSimpleName();
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_SMS, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.CALL_PHONE
    };
    private static final int PERMISSIONS_REQUEST_ACCOUNTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_splash);
        startLoading();

        // MediaSyncTask task = new MediaSyncTask();
        // task.execute();
       // handler.postDelayed(nextActivityLauncher, 2000);
    }

    private void initDB(){
        getBus().register(this);
        mDbHelper = getInstance();
        mDbHelper.initDataBase();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), PERMISSIONS_REQUEST_ACCOUNTS);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(arePermissionsEnabled())
                      //  startLoginActivity();
                        startNextActivity();
                    else
                        requestMultiplePermissions();
                }else
                  //  startLoginActivity();
                    startNextActivity();

            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNextActivity();
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(SplashActivity.this, "앱 사용 허가 승인 불가입니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getBus().unregister(this);
    }

    protected void startNextActivity() {
        /**
         * Add Time Bomb at this point if needed
         */
        initDB();
        Intent intent = new Intent(this, PhoTraceActivity.class);
        startActivity(intent);
        finish();
    }


}
