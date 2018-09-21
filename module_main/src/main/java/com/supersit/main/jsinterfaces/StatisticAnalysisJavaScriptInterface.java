package com.supersit.main.jsinterfaces;

import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.model.UserModel;
import com.supersit.common.provider.IIMService;

/**
 * Created by 田皓午 on 2018/5/11.
 */

public class StatisticAnalysisJavaScriptInterface {

    public StatisticAnalysisJavaScriptInterface() {
        ARouter.getInstance().inject(this);
    }

    @JavascriptInterface
    public long getUserId() {
        return UserModel.getInstance().getCurrUserId();
    }
}
