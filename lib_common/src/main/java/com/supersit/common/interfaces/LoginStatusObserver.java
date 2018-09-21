package com.supersit.common.interfaces;

/**
 * Created by 田皓午 on 2018/5/12.
 */

public interface LoginStatusObserver {

    /**
     * 通知观察者用户已登录
     */
     void onLoginAfter();

    /**
     * 通知观察者用户已注销
     */
    void onLogoutAfter();
}
