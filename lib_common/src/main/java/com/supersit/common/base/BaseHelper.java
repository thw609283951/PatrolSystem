package com.supersit.common.base;

import android.content.Context;
import android.os.Looper;

import com.supersit.common.interfaces.LoginStatusObserver;
import com.threshold.rxbus2.RxBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by 田皓午 on 2018/5/15.
 */

public abstract class BaseHelper implements LoginStatusObserver{

    private CompositeDisposable mCompositeDisposable;
    protected static Context mContext;
    private static Map<String, BaseHelper> mInstanceMap = new HashMap<>();
    protected boolean mIsLogin = false;

    //这样去写单例模式虽然可以省去很多代码，不过因为newInstance方法有限制：构造函数必须public,必须有一个构造函数没有参数
    public static void initialize(Context context) {
        mContext = context;
    }

    public static <T extends BaseHelper> T getInstance(Class<T> model) {
        if(!mInstanceMap.containsKey(model.getSimpleName())){
            synchronized (model){
                try {
                    T instance = model.newInstance();
                    mInstanceMap.put(model.getSimpleName(), instance);
                    return instance;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return (T) mInstanceMap.get(model.getSimpleName());
    }


    //初始化
    protected abstract void init();
    // 同步数据之前的准备
    protected abstract void onSyncDataAfter();
    // 效验数据同步
    protected abstract void checkSyncData();
    // 注销后，重置同步的数据
    protected abstract void resetSyncData();


    @Override
    public void onLoginAfter() {
        mIsLogin = true;
        RxBus.getDefault().register(this);
        onSyncDataAfter();
        checkSyncData();
    }

    @Override
    public void onLogoutAfter() {
        RxBus.getDefault().unregister(this);

        mIsLogin = false;
        resetSyncData();
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
