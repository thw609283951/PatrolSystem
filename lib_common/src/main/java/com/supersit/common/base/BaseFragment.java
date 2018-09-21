package com.supersit.common.base;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supersit.common.R;
import com.supersit.common.interfaces.IBaseFragment;
import com.supersit.common.interfaces.IBaseView;
import com.supersit.mvp.fragment.SuperFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;


/**
 * Fragment顶级父类 : 添加各种状态(数据错误，数据为空，数据加载中)页的展示，
 * 自定义的MaterialDialog的显示，进度条dialog显示
 * <p>
 * MVP模型中把Fragment作为view层，可通过getPresenter()调用对应的presenter实例
 * <p>
 */

@SuppressLint("ValidFragment")
public abstract class BaseFragment<T extends BasePresenter> extends SuperFragment<T> implements IBaseView, IBaseFragment {

    private final String TAG = BaseFragment.class.getSimpleName();

    protected Activity mContext;
    private int mLayoutResId;
    private View mView;

    private Unbinder unbinder;

    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;
    private View rootView;

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

    @SuppressLint("ValidFragment")
    public BaseFragment(){

    }


    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    protected boolean isUseStatusPages(){
        return isUseStatusPages;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initData(getArguments());
        setBaseView(bindLayout());
        initVariable();
    }
    private void setBaseView(@LayoutRes int layoutId) {
        mLayoutResId = layoutId;
    }

    @Nullable
    @Override //container ---> activity
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isUseStatusPages()) {
            addStatusPage(inflater, container);
        } else {
            if (mLayoutResId != 0) {
                mView = inflater.inflate(mLayoutResId, container, false);
            }
        }
        View fillStatusBarView = mView.findViewById(R.id.fillStatusBarView);
        if(null!=fillStatusBarView)
            fillStatusBarView.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        initView(savedInstanceState,view);
        super.onViewCreated(isReuseView ? rootView : view, savedInstanceState);
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }

    }

    protected void setSystemBarTint(View statusBar) {
        if(null !=statusBar){
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) statusBar.getLayoutParams();
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lp.height =  BarUtils.getStatusBarHeight();
            statusBar.setLayoutParams(lp);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
    }

    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }


    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
    }



    /**
     * 添加各种状态页
     */
    private void addStatusPage(LayoutInflater inflater, @Nullable ViewGroup container) {
        mView = inflater.inflate(R.layout.base_status_page, null);
        mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSuperRealContent = (FrameLayout) mView.findViewById(R.id.super_real_content);
        inflater.inflate(mLayoutResId, mSuperRealContent, true);

        mEmptyPage = findViewById(R.id.empty_page);
        mLoadDataButton = findViewById(R.id.error_to_load_button);
        mErrorPage = findViewById(R.id.error_page);
        mLoadingPage = findViewById(R.id.loading_page);
        mCurrentShowView = mLoadingPage;

        mLoadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickErrorLoadData(v);
            }
        });
    }

    public <V extends View> V findViewById(@IdRes int resId) {
        return (V) mView.findViewById(resId);
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
            mProgressDialog = new ProgressDialog(getContext());
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


    public void printDLog(String msg){
        LogUtils.d(msg);
    }

    /**
     *错误Toast：
     */
    public void showErrorToast(int resId) {
        showErrorToast(getString(resId));
    }
    public void showErrorToast(String msg) {
        Toasty.error(getActivity(),msg).show();
    }

    /**
     *成功Toast：
     */
    public void showSuccessToast(int resId) {
        showSuccessToast(getString(resId));
    }
    public void showSuccessToast(String msg) {
        Toasty.success(getActivity(),msg).show();
    }

    /**
     *信息Toast：
     */
    public void showInfoToast(int resId) {
        showInfoToast(getString(resId));
    }
    public void showInfoToast(String msg) {
        Toasty.info(getActivity(),msg).show();
    }
}
