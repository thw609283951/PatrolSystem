package com.supersit.common.base;

import com.supersit.mvp.presenter.SuperPresenter;
import com.threshold.rxbus2.RxBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * MVP模型中的presenter层，通过getView()方法直接调用对应的activity（View层）
 *
 * 继承SuperPresenter 必须保留一个无参的构造方法
 *
 * Created by linlongxin on 2016/8/16.
 */

public class BasePresenter<V> extends SuperPresenter<V>{

    protected String TAG = BasePresenter.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable;

    //在Activity的onStart之后回调，在Fragment的onCreateView之后回调
    public void onCreate(){
        RxBus.getDefault().register(this);
    }

    //在view的onDestroy中调用
    public void onDestroy(){
        RxBus.getDefault().unregister(this);
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    protected void putDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected void removeDisposable(Disposable disposable) {
        if (null!=disposable && null!=mCompositeDisposable) {
            mCompositeDisposable.remove(disposable);
        }
    }
}
