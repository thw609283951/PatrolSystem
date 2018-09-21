package com.supersit.common.interfaces;

import com.blankj.utilcode.util.NetworkUtils;

/**
 * Created by 田皓午 on 2018/5/12.
 */

public interface NetConnectionObserver {

    /**
     * 通知观察者更改状态
     *
     * @param type
     */
    public void updateNetStatus(NetworkUtils.NetworkType type);
}
