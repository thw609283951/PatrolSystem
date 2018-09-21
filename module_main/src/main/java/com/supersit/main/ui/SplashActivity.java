package com.supersit.main.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseActivity;
import com.supersit.common.provider.IIMService;
import com.supersit.main.R;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private static final String TAG = SplashActivity.class.getSimpleName();

	private static final int sleepTime = 2000;

	@Autowired(name = ARouterConstant.IIMService)
	public IIMService mIIMService;

	@Override
	public void initData(Bundle bundle) {
		ARouter.getInstance().inject(this);
	}

	@Override
	public int bindLayout() {
		return R.layout.activity_splash;
	}

	@Override
	public void initView(Bundle savedInstanceState, View view) {
		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		TextView versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}



	@Override
	protected void onStart() {
		super.onStart();
		new Thread(() -> {
            if (mIIMService.isLoggedIn()) {
                // auto login mode, make sure all group and conversation is loaed before enter the main screen
                long start = System.currentTimeMillis();
                long costTime = System.currentTimeMillis() - start;
                //wait
                if (sleepTime - costTime > 0) {
                    try {
                        Thread.sleep(sleepTime - costTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
				MainActivity.start((Activity) mContext);
                finish();
            }else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
				LoginActivity.start(mContext);
                finish();
            }
        }).start();
	}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
	    return AppUtils.getAppVersionName();
	}


}
