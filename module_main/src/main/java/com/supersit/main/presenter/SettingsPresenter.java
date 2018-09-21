package com.supersit.main.presenter;


import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseApplication;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.main.R;
import com.supersit.main.model.ServerModel;
import com.supersit.main.ui.LoginActivity;
import com.supersit.main.ui.MainActivity;
import com.supersit.main.ui.SettingsFragment;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 设置 P
        */

public class SettingsPresenter extends BasePresenter<SettingsFragment> {
    private static final String TAG = SettingsPresenter.class.getSimpleName();

    @Autowired(name = ARouterConstant.IIMService)
    public IIMService mIIMService;

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().inject(this);
    }


    public void logout(){
//        mIIMService.logoutEaseIM();
//        ((MainActivity) getView().getActivity()).finish();
//        getView().startActivity(new Intent(getView().getActivity(), LoginActivity.class));

        mIIMService.logoutEaseIM()
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .compose(RxSchedulers.io_main())
                .subscribe(new Observer<MyResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        getView().showProgressDialog(R.string.Are_logged_out,disposable);
                    }

                    @Override
                    public void onNext(MyResponse value) {
                        BaseApplication.getInstance().noitifyLogoutObserver();
                        (getView().getActivity()).finish();
                        getView().startActivity(new Intent(getView().getActivity(), LoginActivity.class));
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
