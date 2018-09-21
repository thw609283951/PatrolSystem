package com.supersit.main.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.widget.ProgressWebView;
import com.supersit.main.R;
import com.supersit.main.R2;
import com.supersit.main.jsinterfaces.StatisticAnalysisJavaScriptInterface;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class StatisticAnalysisFragment extends BaseFragment {

    private static final String TAG = StatisticAnalysisFragment.class.getSimpleName();

    @BindView(R2.id.webView)
    ProgressWebView webView;

    @BindView(R2.id.error_page)
    LinearLayout errorPage;

    private String mUrl = "file:///android_asset/www/index.html";



    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_webview;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        webView.addJavascriptInterface(new StatisticAnalysisJavaScriptInterface(), "control");
        webView.setLoadListener(new ProgressWebView.OnLoadListener() {
            @Override
            public void loadSuccess() {
                LogUtils.i(TAG+":"+"~"+getString(com.supersit.common.R.string.load_success));
            }

            @Override
            public void loadError() {
                errorPage.setVisibility(View.VISIBLE);
            }
        });
        doBusiness();
    }
    public void doBusiness() {
        webView.loadUrl(mUrl);
    }

    @OnClick(R2.id.error_page)
    public void onViewClicked() {
        webView.loadUrl(mUrl);
        errorPage.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        if(isVisible)
            webView.loadUrl(mUrl);
    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onDestroy() {
        webView.destroy();
        super.onDestroy();

    }
}
