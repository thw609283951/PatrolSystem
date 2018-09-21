package com.supersit.common.interfaces;

/**
 * Created by 田皓午 on 2018/5/12.
 */

public interface LoginStatusSubject {

    /**
     * 注册观察者
     *
     * @param observer
     */
    void addLoginStatusObserver(LoginStatusObserver observer);

    /**
     * 移除观察者
     *
     * @param observer
     */
    void removeLoginStatusObserver(LoginStatusObserver observer);



    /**
     * 用户登录通知
     *
     */
    void noitifyLoginObserver();

    /**
     * 用户注销通知
     *
     */
    void noitifyLogoutObserver();
}