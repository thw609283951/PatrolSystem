package com.supersit.main.presenter;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.model.CommonModel;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.main.R;
import com.supersit.main.model.ServerModel;
import com.supersit.main.ui.ChangePasswordActivity;
import com.supersit.main.ui.LoginActivity;
import com.supersit.main.ui.MainActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 登录 P
        */

public class ChangePasswordPresenter extends BasePresenter<ChangePasswordActivity> {
    private static final String TAG = ChangePasswordPresenter.class.getSimpleName();

    private long mUserId;
    @Autowired(name = ARouterConstant.IIMService)
    public IIMService mIIMService;

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().inject(this);
        mUserId = UserModel.getInstance().getCurrUserId();
    }


    public void changePassword(final String oldPassword, final String newPassword){
        ServerModel.getInstance().changePassword(mUserId,oldPassword,newPassword)
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .concatMap(myResponse -> {
                    return mIIMService.logoutEaseIM();
                })
                .compose(RxSchedulers.io_main())
                .subscribe(new Observer<MyResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getView().showProgressDialog(R.string.Is_change_password,disposable);
                    }

                    @Override
                    public void onNext(MyResponse value) {
                        getView().showSuccessToast(value.getMsg());
                        MainActivity.start(getView());
                        getView().finish();
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
