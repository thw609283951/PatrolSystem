package com.supersit.common;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.baidu.mapapi.SDKInitializer;
import com.raizlabs.android.dbflow.config.CommonGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.supersit.mvp.model.SuperModel;
import com.threshold.rxbus2.RxBus;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by 田皓午 on 2018/4/27.
 */

public class InitIntentService extends IntentService{

    private static final String TAG = InitIntentService.class.getSimpleName();

    public static void start(Context mContext){
        Intent intent = new Intent(mContext,InitIntentService.class);
        mContext.startService(intent);
    }

    public InitIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        SuperModel.initialize(getApplicationContext());

//        FlowManager.init(getApplicationContext());
//        FlowManager.initModule(CommonGeneratedDatabaseHolder.class);// 初始化
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
        SDKInitializer.initialize(getApplicationContext());
    }

}
