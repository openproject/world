package com.tianxia.app.floworld;

import com.tianxia.app.floworld.utils.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class AppBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            AppApplication.mNetWorkState = NetworkUtils.getNetworkState(context);
        }

    }

}
