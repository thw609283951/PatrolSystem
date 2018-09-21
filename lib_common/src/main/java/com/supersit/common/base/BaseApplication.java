package com.supersit.common.base;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDex;


import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.raizlabs.android.dbflow.config.CommonGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.supersit.common.interfaces.LoginStatusObserver;
import com.supersit.common.interfaces.LoginStatusSubject;
import com.supersit.common.interfaces.NetConnectionObserver;
import com.supersit.common.interfaces.NetConnectionSubject;
import com.supersit.mvp.model.SuperModel;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.threshold.rxbus2.RxBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class BaseApplication extends Application implements NetConnectionSubject,LoginStatusSubject {

    private static BaseApplication sInstance;

    private NetworkUtils.NetworkType mNetworkType;
    public List<LoginStatusObserver> mLoginStatusObservers;
    private List<NetConnectionObserver> mNetConnectionObservers;


    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Utils.init(this);
        BaseHelper.initialize(this);
        SuperModel.initialize(this);
        FlowManager.init(this);
        FlowManager.initModule(CommonGeneratedDatabaseHolder.class);// 初始化
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
        SDKInitializer.initialize(getApplicationContext());
        Bugly.init(getApplicationContext(), "aa1f4db4cd", false);
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        // 安装tinker
        Beta.installTinker();
    }

    @Override
    public void addNetObserver(NetConnectionObserver observer) {
        if(null == mNetConnectionObservers)
            mNetConnectionObservers = new ArrayList<>();

        if (!mNetConnectionObservers.contains(observer)) {
            mNetConnectionObservers.add(observer);
        }
    }

    @Override
    public void removeNetObserver(NetConnectionObserver observer) {
        if (mNetConnectionObservers != null && mNetConnectionObservers.contains(observer)) {
            mNetConnectionObservers.remove(observer);
        }
    }

    @Override
    public void notifyNetObserver(NetworkUtils.NetworkType type) {
        if(NetworkUtils.NetworkType.NETWORK_NO == type || NetworkUtils.NetworkType.NETWORK_UNKNOWN == type){

        }else{

        }
        /**
         * 避免多次发送相同的网络状态
         */
        if (mNetworkType == type) {
            return;
        } else {
            mNetworkType = type;
            if (mNetConnectionObservers != null && mNetConnectionObservers.size() > 0) {
                for (NetConnectionObserver observer : mNetConnectionObservers) {
                    observer.updateNetStatus(type);
                }
            }
        }
    }

    @Override
    public void addLoginStatusObserver(LoginStatusObserver observer) {
        if(null == mLoginStatusObservers)
            mLoginStatusObservers = new ArrayList<>();

        if (!mLoginStatusObservers.contains(observer)) {
            mLoginStatusObservers.add(observer);
        }
    }

    @Override
    public void removeLoginStatusObserver(LoginStatusObserver observer) {
        if (null != mLoginStatusObservers && mLoginStatusObservers.contains(observer)) {
            mLoginStatusObservers.remove(observer);
        }
    }

    @Override
    public void noitifyLoginObserver() {
        if (null !=mLoginStatusObservers && mLoginStatusObservers.size() > 0) {
            for (LoginStatusObserver observer : mLoginStatusObservers) {
                observer.onLoginAfter();
            }
        }
    }

    @Override
    public void noitifyLogoutObserver() {
        if (null !=mLoginStatusObservers && mLoginStatusObservers.size() > 0) {
            for (LoginStatusObserver observer : mLoginStatusObservers) {
                observer.onLogoutAfter();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
