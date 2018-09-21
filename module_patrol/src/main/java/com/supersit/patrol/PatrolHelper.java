package com.supersit.patrol;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.PatrolGeneratedDatabaseHolder;
import com.supersit.common.Constant;
import com.supersit.common.base.BaseHelper;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.common.interfaces.LoginStatusObserver;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.patrol.entity.EventTypeEntity;
import com.supersit.patrol.model.PatrolModel;
import com.threshold.rxbus2.RxBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/4/11.
 */

public class PatrolHelper extends BaseHelper{

    protected static final String TAG = PatrolHelper.class.getSimpleName();


    private boolean isSyncingBaseEventsWithServer = false;

    public PatrolHelper() {
    }
    public static PatrolHelper getInstance() {
        return getInstance(PatrolHelper.class);
    }

    @Override
    public void init() {
        FlowManager.initModule(PatrolGeneratedDatabaseHolder.class);// 初始化
    }

    @Override
    public void onSyncDataAfter() {
        isSyncingBaseEventsWithServer =false;
    }


    @Override
    public void resetSyncData() {
        PatrolModel.getInstance().reset();
    }

    @Override
    public void checkSyncData(){
        if(PatrolModel.getInstance().isBaseEventsSynced()){
            LogUtils.d(TAG, "already synced with servre");
        }else{
            if(!PatrolModel.getInstance().isBaseEventsSynced()){
                asyncFetchConferenceParamsFromServer();
            }
        }
    }

    /**
     * 同步操作，获取服务预设巡查事项
     */
    private synchronized void asyncFetchConferenceParamsFromServer(){
        if(isSyncingBaseEventsWithServer)
            return;

        isSyncingBaseEventsWithServer = true;

        long sycnTimeMillis = PatrolModel.getInstance().getLastSycnBaseEventsTimeMillis();
        boolean isNewData = -1 == sycnTimeMillis;
        PatrolModel.getInstance().getBaseEvenst()
                .compose(RxSchedulers.io_io())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<EventTypeEntity>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull MyResponse<List<EventTypeEntity>> value) {
                        if(!mIsLogin) return;

                        List<EventTypeEntity> eventTypeEntities = value.getResultData();
                        PatrolModel.getInstance().saveEventTypeEntitys(isNewData,eventTypeEntities);

                        PatrolModel.getInstance().setLastSycnBaseEventsTimeMillis(System.currentTimeMillis());
                        PatrolModel.getInstance().setBaseEventsSynced(true);
                        postRxBusDataSyncOfBaseEvents(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG,"巡查事项:"+ e.getMessage());
                        isSyncingBaseEventsWithServer = false;
                        postRxBusDataSyncOfBaseEvents(false);
                    }

                    @Override
                    public void onComplete() {
                        isSyncingBaseEventsWithServer = false;
                    }
                });
    }

    public void postRxBusDataSyncOfBaseEvents(boolean isSuccess){
        RxBus.getDefault().post(new RxBusDataSyncEntity(Constant.RXBUS_UPPATROL_BASEEVENTS,isSuccess));
    }

    void showToast(final String message) {
        ToastUtils.showShort(message);
    }
}
