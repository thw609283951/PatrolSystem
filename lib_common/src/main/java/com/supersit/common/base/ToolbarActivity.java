package com.supersit.common.base;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.r0adkll.slidr.Slidr;
import com.supersit.common.R;
import com.supersit.common.utils.StatusBarUtils;
import com.supersit.common.widget.MyLinearLayout;

import butterknife.ButterKnife;


public abstract class ToolbarActivity<T extends BasePresenter> extends BaseActivity<T> {

    private boolean isShowHomeAsUp = true;
    protected Toolbar mToolbar;
    private boolean canSlidr = true;
    private View mRootView;

    //设置 toolbar 是否显示返回键
    protected boolean isShowHomeAsUp() {
        return isShowHomeAsUp;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        StatusBarUtils.transparencyBar(this);
    }

    @Override
    protected void setBaseView(int layoutId) {
        if (isUseStatusPages()) { //添加状态页到activity
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.windowBackg));
            FrameLayout mDecorView = (FrameLayout) getWindow().getDecorView();
            FrameLayout mDecorContent = (FrameLayout) mDecorView.findViewById(android.R.id.content);
            ViewGroup mLayoutView = (ViewGroup) getLayoutInflater().inflate(layoutId, null);
            unbinder = ButterKnife.bind(this,mLayoutView);

            mToolbar = (Toolbar) mLayoutView.findViewById(R.id.toolbar);
            mRootView = mLayoutView.findViewById(R.id.view_root);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(isShowHomeAsUp());
            }
            mLayoutView.removeView(mToolbar);
            if (mToolbar.getParent() != null) {
                ((ViewGroup) mToolbar.getParent()).removeView(mToolbar);
            }
            linearLayout.addView(mToolbar);

            getLayoutInflater().inflate(R.layout.base_status_page, linearLayout, true);
            mSuperRealContent = (FrameLayout) linearLayout.findViewById(R.id.super_real_content);
            mSuperRealContent.addView(mLayoutView);
            mDecorContent.addView(linearLayout);
            initStatusPages(linearLayout);
            mContentView = linearLayout;
        } else {
            super.setBaseView(layoutId);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mRootView = findViewById(R.id.view_root);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(isShowHomeAsUp());
            }
        }

        if(null != mRootView)
            mRootView.setFitsSystemWindows(true);
        if(null != mToolbar)
            mToolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
    }

    public void initStatusPages(LinearLayout parent) {
        mEmptyPage = (TextView) parent.findViewById(R.id.empty_page);
        mLoadDataButton = (TextView) parent.findViewById(R.id.error_to_load_button);
        mErrorPage = (LinearLayout) parent.findViewById(R.id.error_page);
        mLoadingPage = (LinearLayout) parent.findViewById(R.id.loading_page);
        mCurrentShowView = mLoadingPage;
        mLoadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickErrorLoadData(v);
            }
        });
    }

    @Override
    public View findViewById(int id) {
        if (isUseStatusPages()) {
            return mSuperRealContent.findViewById(id);
        } else {
            return super.findViewById(id);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && isShowHomeAsUp()) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mRootView)
            mRootView.setFitsSystemWindows(false);
    }
}
