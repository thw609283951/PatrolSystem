package com.supersit.mvp.presenter;

import java.lang.ref.WeakReference;

/**
 * MVP模型中的presenter层，通过getView()方法直接调用对应的activity（View层）
 *
 * 继承SuperPresenter 必须保留一个无参的构造方法
 *
 * Created by linlongxin on 2016/8/16.
 */

public class SuperPresenter<V> {


    protected WeakReference<V> mMvpView;

    public void attachView(V view) {
        this.mMvpView = new WeakReference<>(view);
    }

    protected V getView() {
        return mMvpView.get();
    }

    //在Activity的onStart之后回调，在Fragment的onCreateView之后回调
    public void onCreate(){
    }

    //在Activity的onResume之后回调，在Fragment的onResume中回调
    public void onResume(){}

    public void onPause(){}

    //在view的onDestroy中调用
    public void onDestroy(){
        if (mMvpView != null) {
            mMvpView.clear();
            mMvpView = null;
        }
    }

}
