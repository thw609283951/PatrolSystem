package com.supersit.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.widget.ProgressWebView;

import butterknife.BindView;
import butterknife.OnClick;

public class WebViewActivity extends ToolbarActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    @BindView(R2.id.webView)
    ProgressWebView webView;

    @BindView(R2.id.error_page)
    LinearLayout errorPage;

    private String title;
    private String url;

    public static void start(Activity mContext, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        if (null == bundle)
            finish();

        title = bundle.getString("title");
        url = bundle.getString("url");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_webview;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(title);
        webView.setLoadListener(new ProgressWebView.OnLoadListener() {
            @Override
            public void loadSuccess() {
                LogUtils.i(TAG+":"+title+"~"+getString(R.string.load_success));
            }

            @Override
            public void loadError() {
                errorPage.setVisibility(View.VISIBLE);
            }
        });
        doBusiness();
    }

    public void doBusiness() {
        webView.loadUrl(url);
    }

    @OnClick(R2.id.error_page)
    public void onViewClicked() {
        webView.loadUrl(url);
        errorPage.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();

    }

}
