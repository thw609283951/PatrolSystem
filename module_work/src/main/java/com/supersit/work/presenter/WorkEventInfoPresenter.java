package com.supersit.work.presenter;

import android.app.Activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.EventHandleEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.work.R;
import com.supersit.work.model.WorkModel;
import com.supersit.work.ui.WorkEventInfoActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class WorkEventInfoPresenter extends BasePresenter<WorkEventInfoActivity> {

    public UserEntity mCurrUser;
    public UserEntity mReceiveUser;
    public String mContent;

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrUser = UserModel.getInstance().getCurrUser();
    }

    public void upLoadEventHandleEntity(int status){
        EventHandleEntity eventHandleEntity = new EventHandleEntity();
        eventHandleEntity.setId(getView().mEventEntity.getEventId());
        eventHandleEntity.setTime(System.currentTimeMillis());
        eventHandleEntity.setSendUserId(mCurrUser.getUserId());
        if(2 == status){
            eventHandleEntity.setReceiveUserId(mReceiveUser.getUserId());
            eventHandleEntity.setMessage(mContent);
            eventHandleEntity.setStatus(2);
        }else{
            eventHandleEntity.setReceiveUserId(mCurrUser.getUserId());
            eventHandleEntity.setMessage("办结");
            eventHandleEntity.setStatus(3);
        }
        WorkModel.getInstance().upLoadEventHandleEntity(eventHandleEntity,null)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getView().showProgressDialog(R.string.loading,d);
                    }

                    @Override
                    public void onNext(MyResponse value) {
                        getView().showSuccessToast(value.getMsg());
                        if(value.isSuccess()){
                            getView().dismissEventHandleDialog();
                            getView().setResult(Activity.RESULT_OK);
                            getView().finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showErrorToast(e.getMessage());
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        getView().dismissProgressDialog();
                    }
                });
    }
}
