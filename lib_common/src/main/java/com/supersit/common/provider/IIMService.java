package com.supersit.common.provider;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.net.model.MyResponse;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/3/31.
 */

public interface IIMService extends IProvider {
    /**
     * 登录即时通讯服务
     * @param username
     * @param password
     * @return
     */
    Observable<MyResponse> loginEaseIM(String username, String password,UserEntity userEntity);

    /**
     * 退出即时通讯服务
     * @return
     */
    Observable<MyResponse> logoutEaseIM();

    /**
     * 监测是否已登录
     * @return
     */
    boolean isLoggedIn();

    /**
     * 获取即时通讯未读信息的数量
     * @return
     */
    int getUnreadIMMsgCountTotal();
}
