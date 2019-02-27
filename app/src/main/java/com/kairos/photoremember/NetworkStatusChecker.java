/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by James on 1/5/15.
 */
public class NetworkStatusChecker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        checkNetwork(context);
    }

    public static void checkNetwork(final Context context) {
        ConnectivityManager mConnManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = mConnManager.getAllNetworkInfo();
        boolean isConneted = false;
        for (NetworkInfo info : networkInfo) {
            if (info == null) {
                continue;
            }
            if (info.getState() == NetworkInfo.State.UNKNOWN) {
                continue;
            }
            isConneted |= (info.getState() == NetworkInfo.State.CONNECTED
                    || info.getState() == NetworkInfo.State.CONNECTING);
        }

        PhoTrace.setNetworkStatus(isConneted);
    }
}
