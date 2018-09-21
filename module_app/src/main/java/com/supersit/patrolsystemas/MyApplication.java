package com.supersit.patrolsystemas;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.supersit.common.base.BaseApplication;
import com.supersit.easeim.IMHelper;
import com.supersit.patrol.PatrolHelper;
import com.supersit.work.WorkHelper;

/**
 * <p>这里仅需做一些初始化的工作</p>
 *
 * @version V1.2.0
 * @name MyApplication
 */
public class MyApplication extends BaseApplication {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (AppUtils.isAppDebug()) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init((Application) mContext);

        addLoginStatusObserver(WorkHelper.getInstance());
        addLoginStatusObserver(PatrolHelper.getInstance());
        addLoginStatusObserver(IMHelper.getInstance());

        WorkHelper.getInstance().init();
        PatrolHelper.getInstance().init();
        IMHelper.getInstance().init();
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // dex突破65535的限制
        MultiDex.install(this);
    }
}
