package com.supersit.mvp;

import android.app.Application;
import android.content.Context;

import com.supersit.mvp.model.SuperModel;

/**
 * Created by Administrator on 2018/3/22.
 */

public class MvpUtils {
    /**
     * 初始化工具类
     *
     */
    public static void init(Application context) {
        SuperModel.initialize(context);
    }
}
