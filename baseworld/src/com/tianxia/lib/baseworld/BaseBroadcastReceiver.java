package com.tianxia.lib.baseworld;

import com.tianxia.lib.baseworld.utils.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class BaseBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            BaseApplication.mNetWorkState = NetworkUtils.getNetworkState(context);
        }

    }

}
