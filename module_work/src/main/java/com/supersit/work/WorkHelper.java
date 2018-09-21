package com.supersit.work;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.WorkGeneratedDatabaseHolder;
import com.supersit.common.Constant;
import com.supersit.common.base.BaseHelper;
import com.supersit.common.entity.CmdMessage;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.work.entity.ApplicationEntity;
import com.supersit.work.entity.NotificationEntity;
import com.supersit.work.model.WorkModel;
import com.threshold.rxbus2.RxBus;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/4/20.
 */

public class WorkHelper extends BaseHelper{
    protected static final String TAG = WorkHelper.class.getSimpleName();

    private WorkNotify mWorkNotify;
    private boolean isSyncingAppsWithServer = false;

    public WorkHelper() {
    }
    public static WorkHelper getInstance() {
        return getInstance(WorkHelper.class);
    }

    @Override
    public void init() {
        FlowManager.initModule(WorkGeneratedDatabaseHolder.class);// 初始化
        getWorkNotify();
    }

    @Override
    protected void onSyncDataAfter() {
        isSyncingAppsWithServer =false;
    }

    @Override
    protected void checkSyncData() {
        if(WorkModel.getInstance().isAppsSynced()){
            LogUtils.d(TAG, "already synced with servre");
        }else{
            if(!WorkModel.getInstance().isAppsSynced()){
                asyncFetchAppListFromServer();
            }
        }
    }

    @Override
    protected void resetSyncData() {
        WorkModel.getInstance().reset();
    }


    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void RecRxBusCmdMessage(CmdMessage cmdMessage) {
      try {
          switch (cmdMessage.getMessage()){
              case "upFunctionList":
                  isSyncingAppsWithServer = false;
                  WorkModel.getInstance().setLastSycnAppsTimeMillis(-1);
                  asyncFetchAppListFromServer();
                  break;

              case "newNotice":
                  recNewNotification(cmdMessage.getData());
                  break;

              case "urgentEvent":
                  getWorkNotify().sendWorkEventNotification();
                  break;
              default:
                  break;
          }
      }catch (Exception e){
          e.printStackTrace();
      }
    }

    private void recNewNotification(JsonObject jsonObject)throws Exception{
        NotificationEntity notificationEntity =  new Gson().fromJson(jsonObject,NotificationEntity.class);
//        notificationEntity.save();
        getWorkNotify().sendNotification(notificationEntity);
//        RxBus.getDefault().post(new RxBusDataSyncEntity(Constant.RXBUS_UPWORK_NOTIFYS,true));
    }



    /**
     * 同步操作，获取 可查看的应用列表
     */
    public synchronized void asyncFetchAppListFromServer(){
        if(isSyncingAppsWithServer)
            return;

        isSyncingAppsWithServer = true;
        long sycnAppsTimeMillis = WorkModel.getInstance().getLastSycnAppsTimeMillis();
        boolean isNewData = -1 == sycnAppsTimeMillis;
        long userId = UserModel.getInstance().getCurrUserId();
        WorkModel.getInstance().getApplicationEntitysByServer(userId,sycnAppsTimeMillis)
                .compose(RxSchedulers.io_io())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<ApplicationEntity>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull MyResponse<List<ApplicationEntity>> value) {
                        if(!mIsLogin) return;

                        List<ApplicationEntity> applicationEntities = value.getResultData();
                        WorkModel.getInstance().saveApplicationEntitys(isNewData,applicationEntities);

                        WorkModel.getInstance().setAppsSynced(true);
                        WorkModel.getInstance().setLastSycnAppsTimeMillis(System.currentTimeMillis());
                        postRxBusDataSyncOfApps(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                        isSyncingAppsWithServer = false;
                        postRxBusDataSyncOfApps(false);
                    }

                    @Override
                    public void onComplete() {
                        isSyncingAppsWithServer = false;
                    }
                });
    }

    public void postRxBusDataSyncOfApps(boolean isSuccess){
        RxBus.getDefault().post(new RxBusDataSyncEntity(Constant.RXBUS_UPWORK_APPS,isSuccess));
    }

    void showToast(final String message) {
        ToastUtils.showShort(message);
    }

    public WorkNotify getWorkNotify(){
        if(null == mWorkNotify){
            mWorkNotify=new WorkNotify();
            mWorkNotify.init(mContext);
        }
        return mWorkNotify;
    }
}

