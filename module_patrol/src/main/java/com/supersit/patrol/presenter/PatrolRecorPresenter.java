package com.supersit.patrol.presenter;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.ui.PatrolRecordActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PatrolRecorPresenter extends BasePresenter<PatrolRecordActivity> {


    public void getPatrolRecord(boolean isRefresh,long lastTimeMillis,String msg){
        PatrolModel.getInstance().getPatrolRecord(getView().mForm,getView().mId,lastTimeMillis,msg)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<EventEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MyResponse<List<EventEntity>> value) {
                        getView().loadSuccess(isRefresh,value.getResultData());
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
