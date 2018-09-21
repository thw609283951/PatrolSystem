package com.supersit.work.presenter;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.work.WorkHelper;
import com.supersit.work.model.WorkModel;
import com.supersit.work.ui.WorkEventListActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/4/20.
 */

public class WorkEventListPresenter extends BasePresenter<WorkEventListActivity>{

    private List<EventEntity> mWorkEventEntitys;
    private WorkEventsSyncListener mWorkEventsSyncListener;

    private long mUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        mUserId = UserModel.getInstance().getCurrUserId();

//        if(!WorkModel.getInstance().isWorkEventsSynced()){
//            mWorkEventsSyncListener = new WorkEventsSyncListener();
//            WorkHelper.getInstance().addSyncWorkEventsListener(mWorkEventsSyncListener);
//        }else{
//            initWorkEvents();
//        }
    }

//    public void initWorkEvents(){
//        if(null == mWorkEventEntitys)
//            mWorkEventEntitys = WorkModel.getInstance().getWorkEventsByStatus(getView().mWorkEventStatus);
//
//        getView().refreshList(mWorkEventEntitys);
//    }
    public void getWorkEvents(boolean isRefresh,int eventStatus,long lastTimeMillis,String msg){
        WorkModel.getInstance().getWorkEventsByServer(mUserId,eventStatus, lastTimeMillis, msg)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<EventEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MyResponse<List<EventEntity>> value) {
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



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    class WorkEventsSyncListener implements DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            LogUtils.d(TAG, "on work event list sync success:" + success);
//            initWorkEvents();
        }
    }

}
