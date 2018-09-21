package com.supersit.common.widget;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.supersit.common.R;


/**
 * Created by Administrator on 2017/1/11.
 */

public class ProgressWebView extends WebView {
    private ProgressBar progressbar;

    private boolean loadError=false;

    OnLoadListener mOnLoadListener;

    public interface OnLoadListener{
        void loadSuccess();
        void loadError();
    }

    public void setLoadListener(OnLoadListener mOnLoadListener){
        this.mOnLoadListener=mOnLoadListener;
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        ClipDrawable d = new ClipDrawable(new ColorDrawable(getResources().getColor(R.color.orange)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progressbar.setProgressDrawable(d);
        addView(progressbar);

        getSettings().setJavaScriptEnabled(true);
        this.setWebViewClient(new WebViewClient());
        this.setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
            if(!TextUtils.isEmpty(title)&&title.toLowerCase().contains("error")){
                loadError = true;
            }
        }

    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            if(loadError){
                if(mOnLoadListener!=null)
                    mOnLoadListener.loadError();
            }else{
                if(mOnLoadListener!=null)
                    mOnLoadListener.loadSuccess();
            }

        }
        /**
         * 页面加载错误时执行的方法，但是在6.0以下，有时候会不执行这个方法
         * @param view
         * @param errorCode
         * @param description
         * @param failingUrl
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadError = true;
        }

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

}
