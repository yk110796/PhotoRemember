package com.yhsoft.photoremember;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.NetworkInfo.State;
import static android.net.NetworkInfo.State.CONNECTED;
import static android.net.NetworkInfo.State.CONNECTING;
import static android.net.NetworkInfo.State.UNKNOWN;
import static com.yhsoft.photoremember.PhoTrace.setNetworkStatus;

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
                .getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = mConnManager.getAllNetworkInfo();
        boolean isConneted = false;
        for (NetworkInfo info : networkInfo) {
            if (info == null) {
                continue;
            }
            if (info.getState() == UNKNOWN) {
                continue;
            }
            isConneted |= (info.getState() == CONNECTED
                    || info.getState() == CONNECTING);
        }

        setNetworkStatus(isConneted);
    }
}
