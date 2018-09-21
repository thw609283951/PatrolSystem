package com.supersit.common.interfaces;

import com.blankj.utilcode.util.NetworkUtils;

/**
 * Created by 田皓午 on 2018/5/12.
 */

public interface NetConnectionSubject {

    /**
     * 注册观察者
     *
     * @param observer
     */
    void addNetObserver(NetConnectionObserver observer);

    /**
     * 移除观察者
     *
     * @param observer
     */
    void removeNetObserver(NetConnectionObserver observer);

    /**
     * 状态更新通知
     *
     * @param networkType
     */
    void notifyNetObserver(NetworkUtils.NetworkType networkType);
}