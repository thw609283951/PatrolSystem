package com.supersit.easeim.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hyphenate.chat.EMClient;
import com.supersit.common.ARouterConstant;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.easeim.model.IMModel;

import io.reactivex.Observable;

/**
 * Created by 田皓午 on 2018/3/31.
 */
@Route(path = ARouterConstant.IIMService, name = "用户信息服务")
public class IMService implements IIMService {
    @Override
    public Observable<MyResponse> loginEaseIM(String username, String password,UserEntity userEntity) {
        return IMModel.getInstance().loginEaseIM(username,password,userEntity);
    }

    @Override
    public Observable<MyResponse> logoutEaseIM() {
        return IMModel.getInstance().logoutEaseIM();
    }


    @Override
    public boolean isLoggedIn() {
        return IMModel.getInstance().isLoggedIn();
    }

    @Override
    public int getUnreadIMMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    @Override
    public void init(Context context) {

    }
}
