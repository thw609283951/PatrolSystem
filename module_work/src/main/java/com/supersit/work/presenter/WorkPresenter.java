package com.supersit.work.presenter;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.supersit.common.Constant;
import com.supersit.common.base.BaseActivity;
import com.supersit.common.base.BaseApplication;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.CmdMessage;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.common.interfaces.NetConnectionObserver;
import com.supersit.common.interfaces.NetConnectionSubject;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.work.WorkHelper;
import com.supersit.work.entity.ApplicationEntity;
import com.supersit.work.entity.WeatherEntity;
import com.supersit.work.model.WorkModel;
import com.supersit.work.ui.WorkFragment;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/4/20.
 */

public class WorkPresenter extends BasePresenter<WorkFragment> implements NetConnectionObserver{

    private UserEntity mCurrUser;

    private boolean mIsInitWeather = false;
    private boolean mIsInitApps = false;
    private boolean mIsInitUnReadCount = false;
    private boolean mIsInitPatrolDynamic = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrUser = UserModel.getInstance().getCurrUser();

        if(WorkModel.getInstance().isAppsSynced()){
            initAppList();
        }

        BaseApplication.getInstance().addNetObserver(this);

        initWeather();
        initWorkEventUnReadCount();
        initPatrolDynamic();
    }


    public void initWeather(){
        WorkModel.getInstance().getWeatherInfo(101280103)
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .compose(RxSchedulers.io_main())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull JsonObject value) {
                        try {
                            int status = value.get("status").getAsInt();
                            if(1000 == status){
                                WeatherEntity weatherEntity = new Gson().fromJson(value.get("data"),WeatherEntity.class);
                                getView().refreshWeather(weatherEntity);
                            }else{
                                String desc = value.get("desc").getAsString();
                                getView().showErrorToast(desc);
                                getView().refreshWeather(null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            getView().refreshWeather(null);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                        getView().showErrorToast(e.getMessage());
                        getView().refreshWeather(null);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void initWorkEventUnReadCount(){

        WorkModel.getInstance().getWorkEventsSize(mCurrUser.getUserId())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .compose(RxSchedulers.io_main())
                .subscribe(new Observer<MyResponse<JsonObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull MyResponse<JsonObject> value) {
                        int waitAcceptUnreadCount = 0;
                        int processingUnreadCoun = 0;
                        try {
                            JsonObject object = value.getResultData();
                            waitAcceptUnreadCount = object.get("pendingCount").getAsInt();
                            processingUnreadCoun = object.get("handingCount").getAsInt();
                        } catch (Exception e) {
                            e.printStackTrace();
                            getView().showErrorToast(e.getMessage());
                        }
                        getView().refreshWaitAcceptUnreadCount( waitAcceptUnreadCount);
                        getView().refreshProcessingUnreadCount( processingUnreadCoun);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void initAppList(){
        mIsInitApps=true;
        List<ApplicationEntity> applicationEntities = WorkModel.getInstance().getApplicationEntitys();
        getView().refreshAppList(applicationEntities);
    }

    public void initPatrolDynamic(){
        WorkModel.getInstance().getPatrolDynamicByServer(mCurrUser.getUserId())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .compose(RxSchedulers.io_main())
                .subscribe(new Observer<MyResponse<List<EventEntity>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull MyResponse<List<EventEntity>> value) {
                        mIsInitPatrolDynamic = true;
                        getView().refreshPatrolDynamicList(value.getResultData());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void addPatrolDynamic(JsonObject jsonObject){
        try {
            EventEntity eventEntity =  new Gson().fromJson(jsonObject,EventEntity.class);
            getView().addPatrolDynamicList(eventEntity);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void RecRxBusDataSync(RxBusDataSyncEntity syncEntity) {
        if(Constant.RXBUS_UPWORK_APPS== syncEntity.getId()){
            if(syncEntity.isSuccess()){
                initAppList();
            }
        }
    }


    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void recCmdMessage(CmdMessage cmdMessage) {
        switch (cmdMessage.getMessage()){
            case "insertWorkpastStatus":
                initWorkEventUnReadCount();
                break;
            case "insertWorkpast":
                addPatrolDynamic((JsonObject)
                        cmdMessage.getData());
                break;
            default:break;
        }
    }

    @Override
    public void updateNetStatus(NetworkUtils.NetworkType type) {
        if(NetworkUtils.NetworkType.NETWORK_NO == type || NetworkUtils.NetworkType.NETWORK_UNKNOWN == type){
            return;
        }
        if(!mIsInitWeather)
            initWeather();
        if(!mIsInitApps)
            WorkHelper.getInstance().asyncFetchAppListFromServer();
        if(!mIsInitUnReadCount)
            initWorkEventUnReadCount();
        if(!mIsInitPatrolDynamic)
            initPatrolDynamic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().removeNetObserver(this);
    }
}
