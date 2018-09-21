package com.supersit.main.model;

import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.utils.RxUtils;
import com.supersit.mvp.model.SuperModel;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * Created by 田皓午 on 2018/3/31.
 */

public class ServerModel extends SuperModel {
    protected String TAG = ServerModel.class.getSimpleName();


    public ServerModel() {
    }

    @Override
    public void reset() {

    }

    public static ServerModel getInstance() {
        return getInstance(ServerModel.class);
    }

    public Observable<MyResponse<UserEntity>> loginServer(String username, String password){
        HttpParams params = new HttpParams();
        params.put("userAccount", username);
        params.put("userPassWord", password);
        params.put("deviceId", Settings.Secure.getString(Utils.getApp().getContentResolver(), Settings.Secure.ANDROID_ID));
        Type type = new TypeToken<MyResponse<UserEntity>>() {}.getType();

        return RxUtils.request(HttpMethod.POST, Urls.URL_LOGIN, type,params);
    }

    public Observable<MyResponse> changePassword(long userId, String oldPassword,String newPassword){
        HttpParams params = new HttpParams();
        StringBuffer url = new StringBuffer(Urls.URL_CHANGE_PASSWORK);
        url.append(userId);
        url.append("/");
        url.append(oldPassword);
        url.append("/");
        url.append(newPassword);

        Type type = new TypeToken<MyResponse>() {}.getType();
        return RxUtils.request(HttpMethod.GET, url.toString(), type,params);
    }
}
