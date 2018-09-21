package com.supersit.patrol.presenter;


import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.ui.PatrolRecordActivity;
import com.supersit.patrol.ui.ProjectListActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ProjectListPresenter extends BasePresenter<ProjectListActivity> {

    @Override
    public void onCreate() {
        super.onCreate();

    }


    public void getProjectList(){
        PatrolModel.getInstance().getProjectList(UserModel.getInstance().getCurrUserId())
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<DeptEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MyResponse<List<DeptEntity>> value) {
                        getView().loadSuccess(value.getResultData());
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().loadError();
                    }

                    @Override
                    public void onComplete() {}
                });
    }
}
