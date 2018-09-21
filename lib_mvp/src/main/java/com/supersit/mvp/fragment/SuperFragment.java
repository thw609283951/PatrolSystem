package com.supersit.mvp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.mvp.presenter.SuperPresenter;

import java.lang.annotation.Annotation;


/**
 * Fragment顶级父类 : 添加各种状态(数据错误，数据为空，数据加载中)页的展示，
 * 自定义的MaterialDialog的显示，进度条dialog显示
 * <p>
 * MVP模型中把Fragment作为view层，可通过getPresenter()调用对应的presenter实例
 * <p>
 */

@SuppressLint("ValidFragment")
public abstract class SuperFragment<T extends SuperPresenter> extends Fragment{

    private final String TAG = "SuperFragment";



    private T mPresenter;

    @SuppressLint("ValidFragment")
    public SuperFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter();
    }

 /*   @Nullable
    @Override //container ---> activity
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mLayoutResId != 0) {
            mView = inflater.inflate(mLayoutResId, container, false);
        }
        return mView;
    }*/

    // onCreateView 之后回调
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialView();
        if (null != mPresenter)
            mPresenter.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mPresenter)
            mPresenter.onResume();
    }

    public void attachPresenter() {
        Annotation[] annotations = getClass().getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequirePresenter) {
                    RequirePresenter presenter = (RequirePresenter) annotation;
                    try {
                        mPresenter = (T) presenter.value().newInstance();
                        mPresenter.attachView(this);
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                        Log.i(TAG,"SuperFragment : " + e.getMessage());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.i(TAG,"SuperFragment : " + e.getMessage());
                    }
                }
            }
        }
    }

    public T getPresenter() {
        return mPresenter;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != mPresenter)
            mPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.onDestroy();
        }
        mPresenter = null;
    }

    public void onInitialView(){}
}
