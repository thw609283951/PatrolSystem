package com.supersit.work.presenter;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonObject;
import com.supersit.common.Constant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.CmdMessage;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.work.entity.NotificationEntity;
import com.supersit.work.model.WorkModel;
import com.supersit.work.ui.NotificationListActivity;
import com.supersit.work.ui.WorkEventListActivity;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/4/20.
 */

public class NotificationListPresenter extends BasePresenter<NotificationListActivity>{

    private long mUserId;
    @Override
    public void onCreate() {
        super.onCreate();
        mUserId = UserModel.getInstance().getCurrUserId();
    }

    public void getNotifications(boolean isRefresh,long lastTimeMillis){
        WorkModel.getInstance().getNotificationsByServer(mUserId, lastTimeMillis)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<NotificationEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MyResponse<List<NotificationEntity>> value) {
                        if(value.isSuccess()){
                            getView().loadSuccess(isRefresh,value.getResultData());
                        }else{
                            getView().loadError(isRefresh);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().loadError(isRefresh);
                    }

                    @Override
                    public void onComplete() {}
                });

    }
}
