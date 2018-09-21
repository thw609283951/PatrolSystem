package com.supersit.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.supersit.common.base.BaseApplication;

/**
 * Created by 田皓午 on 2018/5/12.
 */

public class NetConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkUtils.NetworkType connectionType = NetworkUtils.getNetworkType();

            /**
             * 更改网络状态
             */
            if (BaseApplication.getInstance() != null) {
                BaseApplication.getInstance().notifyNetObserver(connectionType);
            }
        }
    }
}