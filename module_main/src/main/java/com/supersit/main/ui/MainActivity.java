package com.supersit.main.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.adapter.FragmentAdapter;
import com.supersit.common.base.BaseActivity;
import com.supersit.common.provider.IIMService;
import com.supersit.common.widget.NoScrollViewPager;
import com.supersit.main.R;
import com.supersit.main.R2;
import com.supersit.main.presenter.MainPresenter;
import com.supersit.mvp.presenter.RequirePresenter;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

@RequirePresenter(MainPresenter.class)
@Route(path = ARouterConstant.MainActivity)
public class MainActivity extends BaseActivity<MainPresenter> {

    @BindArray(R2.array.tab_main_titles)
    public String[] mTabTitles;

    public int[] mTabNormalIcons={R.mipmap.ic_instant_messaging_unselect,R.mipmap.ic_patrol_inspection_unselect
            ,R.mipmap.ic_routine_work_unselect,R.mipmap.ic_statistic_analysis_unselect,R.mipmap.ic_my_info_unselect};

    public int[] mTabPressedIcons={R.mipmap.ic_instant_messaging_select,R.mipmap.ic_patrol_inspection_select
            ,R.mipmap.ic_routine_work_select,R.mipmap.ic_statistic_analysis_select,R.mipmap.ic_my_info_select};


    @BindView(R2.id.vp_main)
    NoScrollViewPager mVpMain;
    @BindView(R2.id.tab_main)
    CommonTabLayout mTabMain;

    @Autowired(name = ARouterConstant.IIMService)
    public IIMService mIIMService;

    private boolean mIsConflict = false;
    private boolean mIsCurrentAccountRemoved = false;

    public static void start(Activity context){
        Intent intent=new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        ARouter.getInstance().inject(this);
        if (getIntent() != null && (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) ||
                        getIntent().getBooleanExtra(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD, false) ||
                        getIntent().getBooleanExtra(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE, false))) {
            if(null != mIIMService)
                mIIMService.logoutEaseIM();
            finish();
            LoginActivity.start(mContext);
            return;
        } else if (getIntent() != null &&
                getIntent().getBooleanExtra(Constant.ACCOUNT_IS_CONFLICT, false)) {
            if(null != mIIMService)
                mIIMService.logoutEaseIM();
            finish();
            LoginActivity.start(mContext);
            return;
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mVpMain.setPagerEnabled(false);

    }

    public void initFragments(List<Fragment> fragments, ArrayList<CustomTabEntity> tabEntities) {
        mVpMain.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        mVpMain.setOffscreenPageLimit(4);
        mTabMain.setTabData(tabEntities);
        mTabMain.setOnTabSelectListener(mOnTabSelectListener);
        mTabMain.setCurrentTab(2);
        mVpMain.setCurrentItem(2);

    }

    private OnTabSelectListener mOnTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            mVpMain.setCurrentItem(position);
        }

        @Override
        public void onTabReselect(int position) {

        }
    };

    public void updateUnreadLabel(int count) {
        if(count > 0 ){
            mTabMain.showMsg(0,count);
        }else{
            mTabMain.hideMsg(0);
        }
    }

    private int getExceptionMessageId(String exceptionType) {
        if(exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
            return R.string.connect_conflict;
        } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
            return R.string.em_user_remove;
        } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
            return R.string.user_forbidden;
        }
        return R.string.Network_error;
    }

    private boolean mIsExceptionDialogShow = false;
    private AlertDialog.Builder exceptionBuilder;
    /**
     * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
     */
    private void showExceptionDialog(String exceptionType) {
        mIsExceptionDialogShow = true;
        getPresenter().mIIMService.logoutEaseIM();
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new AlertDialog.Builder(mContext);
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));
                exceptionBuilder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    exceptionBuilder = null;
                    mIsExceptionDialogShow = false;
                    finish();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.create().show();
                mIsConflict = true;
            } catch (Exception e) {
                LogUtils.e( "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    private void showExceptionDialogFromIntent(Intent intent) {
        if(null != mIIMService)
            mIIMService.logoutEaseIM();
        LogUtils.e("showExceptionDialogFromIntent");
        if (!mIsExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } else if (!mIsExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!mIsExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD, false) ||
                intent.getBooleanExtra(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE, false)) {
            this.finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showExceptionDialogFromIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constant.ACCOUNT_IS_CONFLICT, mIsConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, mIsCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
