package com.supersit.common.base;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supersit.common.interfaces.IBaseView;
import com.supersit.common.utils.StatusBarUtils;
import com.supersit.common.utils.ViewManager;
import com.supersit.mvp.SuperActivity;
import com.supersit.common.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;


/**
 * Activity 顶级父类 : 添加各种状态 ( 数据错误，数据为空，数据加载中 ) 页的展示，
 * 自定义的 MaterialDialog 的显示，进度条 dialog 显示
 * <p>
 * MVP模型中把Activity作为view层，可通过getPresenter()调用对应的presenter实例
 * <p>
 * Created by linlongxin on 2016/8/3.
 */

public abstract class BaseActivity<T extends BasePresenter> extends SuperActivity<T>implements IBaseView {

    private final String TAG = BaseActivity.class.getSimpleName();

    protected Context mContext;

    protected Unbinder unbinder;
    /**
     * 当前Activity渲染的视图View
     */
    protected View mContentView;

    private boolean isUseStatusPages = false;
    private boolean isShowLoading = true;
    private boolean isShowingContent = false;
    private boolean isShowingError = false;

    protected TextView mEmptyPage;
    protected TextView mLoadDataButton;
    protected LinearLayout mErrorPage;
    protected LinearLayout mLoadingPage;
    protected ViewGroup mSuperRealContent; //命名为了避免其子类中有相同
    protected View mCurrentShowView;

    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏属性
        initSystemBarTint();
        ViewManager.getInstance().addActivity(this);

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        initData(bundle);
        setBaseView(bindLayout());
        initView(savedInstanceState, mContentView);
    }

    protected void setBaseView(@LayoutRes int layoutId) {
        mContentView = LayoutInflater.from(this).inflate(layoutId, null);
        if (isUseStatusPages) {
            addStatusPage(layoutId);
        } else{
            super.setContentView(mContentView);
        }
        unbinder = ButterKnife.bind(this,mContentView);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        ViewManager.getInstance().finishActivity(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        StatusBarUtils.transparencyBar(this);
        StatusBarUtils.StatusBarLightMode(this);
    }

    protected void hideSoftKeyboard() {
        KeyboardUtils.hideSoftInput(this);
    }

    public boolean isUseStatusPages() {
        return isUseStatusPages;
    }
    /**
     * 添加各种状态页
     *
     * @param contentID
     */
    private void addStatusPage(@LayoutRes int contentID) {
        FrameLayout mDecorContent = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        getLayoutInflater().inflate(R.layout.base_status_page, mDecorContent, true);
        mSuperRealContent = (FrameLayout) mDecorContent.findViewById(R.id.super_real_content); //Activity的content
        getLayoutInflater().inflate(contentID, mSuperRealContent, true); //把activity要显示的xml加载到mContent布局里

        mEmptyPage = (TextView) mDecorContent.findViewById(R.id.empty_page); //事实说明view状态时GONE也可以findViewById()
        mLoadDataButton = (TextView) mDecorContent.findViewById(R.id.error_to_load_button);
        mErrorPage = (LinearLayout) mDecorContent.findViewById(R.id.error_page);
        mLoadingPage = (LinearLayout) mDecorContent.findViewById(R.id.loading_page);
        mCurrentShowView = mLoadingPage;

        mLoadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickErrorLoadData(v);
            }
        });
    }

    public void onClickErrorLoadData(View v) {
        showLoading();
    }

    public void showEmpty() {
        showView(mEmptyPage);
        isShowingError = false;
        isShowingContent = false;
        isShowLoading = false;
    }

    public void showError() {
        if (!isShowingError) {
            showView(mErrorPage);
            isShowingError = true;
            isShowingError = false;
            isShowLoading = false;
        }
    }

    public void showLoading() {
        if (!isShowLoading) {
            showView(mLoadingPage);
            isShowingContent = false;
            isShowingError = false;
            isShowLoading = true;
        }
    }

    public void showContent() {
        if (!isShowingContent) {
            showView(mSuperRealContent);
            isShowingContent = true;
            isShowingError = false;
            isShowLoading = false;
        }
    }

    public void showView(View view) {
        hideViewWithAnimation(mCurrentShowView);
        mCurrentShowView = view;
        view.setVisibility(View.VISIBLE);
        showViewWithAnimation(view);
    }

    /**
     * 展示状态页添加动画
     *
     * @param view
     */
    public void showViewWithAnimation(View view) {
        if (mShowAnimator != null) {
            mShowAnimator.end();
            mShowAnimator.cancel();
            mShowAnimator = null;
        }
        mShowAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        mShowAnimator.setDuration(400);
        mShowAnimator.start();
    }

    /**
     * 隐藏状态页添加动画
     *
     * @param view
     */
    public void hideViewWithAnimation(View view) {
        if (mHideAnimator != null) {
            mHideAnimator.end();
            mHideAnimator.cancel();
            mHideAnimator = null;
        }
        mHideAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        mHideAnimator.setDuration(400);
        mHideAnimator.start();
        view.setVisibility(View.GONE);
    }

    private ProgressDialog mProgressDialog;

    /**
     * 获取进度条的dialog
     */
    public void showProgressDialog() {
        showProgressDialog(null);
    }
    public void showProgressDialog(Disposable disposable) {
        showProgressDialog(R.string.data_loading,disposable);
    }

    public void showProgressDialog(int resString,Disposable disposable) {
        showProgressDialog(getString(resString),disposable);
    }

    public void showProgressDialog(String sHint,Disposable disposable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(dialog -> getPresenter().removeDisposable(disposable));
        }
        mProgressDialog.setMessage(sHint);
        mProgressDialog.show();
    }

    public void setProgressDialogHint(String sHint) {
        setProgressDialogHint(sHint,null);
    }

    public void setProgressDialogHint(String sHint,Disposable disposable) {
        if (mProgressDialog == null) {
            showProgressDialog(sHint,disposable);
        } else {
            mProgressDialog.setMessage(sHint);
        }
    }

    public boolean isShowProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing())
            return false;
        return true;
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     *错误Toast：
     */
    public void showErrorToast(int resId) {
        showErrorToast(getString(resId));
    }
    public void showErrorToast(String msg) {
        Toasty.error(mContext,msg).show();
    }

    /**
     *成功Toast：
     */
    public void showSuccessToast(int resId) {
        showSuccessToast(getString(resId));
    }
    public void showSuccessToast(String msg) {
        Toasty.success(mContext,msg).show();
    }

    /**
     *信息Toast：
     */
    public void showInfoToast(int resId) {
        showInfoToast(getString(resId));
    }
    public void showInfoToast(String msg) {
        Toasty.info(mContext,msg).show();
    }

    public void printDLog(String msg){
        LogUtils.d(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager=getSupportFragmentManager();
        for(int indext=0;indext<fragmentManager.getFragments().size();indext++)
        {
            Fragment fragment=fragmentManager.getFragments().get(indext); //找到第一层Fragment
            if(fragment==null)
                Log.w(TAG, "Activity result no fragment exists for index: 0x"
                        + Integer.toHexString(requestCode));
            else
                handleResult(fragment,requestCode,resultCode,data);
        }
    }
    /**
     * 递归调用，对所有的子Fragment生效
     * @param fragment
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment fragment,int requestCode,int resultCode,Intent data)
    {
        fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult
        Log.e(TAG, "MyBaseFragmentActivity");
        List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment
        if(childFragment!=null)
            for(Fragment f:childFragment)
                if(f!=null)
                {
                    handleResult(f, requestCode, resultCode, data);
                }
        if(childFragment==null)
            Log.e(TAG, "MyBaseFragmentActivity1111");
    }

}
