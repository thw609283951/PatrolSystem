package com.supersit.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.mvp.presenter.SuperPresenter;

import java.lang.annotation.Annotation;


/**
 * Activity 顶级父类 : 添加各种状态 ( 数据错误，数据为空，数据加载中 ) 页的展示，
 * 自定义的 MaterialDialog 的显示，进度条 dialog 显示
 * <p>
 * MVP模型中把Activity作为view层，可通过getPresenter()调用对应的presenter实例
 * <p>
 * Created by linlongxin on 2016/8/3.
 */

public class SuperActivity<P extends SuperPresenter> extends AppCompatActivity {

    private final String TAG = "SuperActivity";

    private P mPresenter;

    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
        mContext = this;
        attachPresenter();
    }

    public <V extends View> V $(@IdRes int id) {
        return (V) super.findViewById(id);
    }

    //在onStart之后回调
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onCreate();
        }

    }

    //在onResume之后回调
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }


    public void attachPresenter() {
        Annotation[] annotations = getClass().getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequirePresenter) {
                    RequirePresenter presenter = (RequirePresenter) annotation;
                    try {
                        mPresenter = (P) presenter.value().newInstance();
                        mPresenter.attachView(this);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        Log.i(TAG, "SuperActivity : " + e.getMessage());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.i(TAG, "SuperActivity : " + e.getMessage());
                    }
                }
            }
        }
    }

    public P getPresenter() {
        return mPresenter;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        mPresenter = null;
    }
}