package com.supersit.main.ui;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.SpannableStringUtil;
import com.supersit.main.R;
import com.supersit.main.R2;
import com.supersit.main.presenter.SettingsPresenter;
import com.supersit.mvp.presenter.RequirePresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
@RequirePresenter(SettingsPresenter.class)
public class SettingsFragment extends BaseFragment<SettingsPresenter> {


    @BindView(R2.id.nv_settings)
    NavigationView nvSettings;
    @BindView(R2.id.tv_username)
    TextView tvUsername;
    @BindView(R2.id.iv_avater)
    ImageView ivAvater;
    @BindView(R2.id.tv_dept)
    TextView tvDept;



    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_settings;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        nvSettings.setItemIconTintList(null);
        nvSettings.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        UserEntity userEntity = UserModel.getInstance().getCurrUser();
        tvUsername.setText(userEntity.getNickName());
        if (null != userEntity.getDepts() && !userEntity.getDepts().isEmpty()) {
            SpannableStringUtil sDeptNames = new SpannableStringUtil();
            for (int i = 0; i < userEntity.getDepts().size(); i++) {
                DeptEntity deptEntitiy = (DeptEntity)userEntity.getDepts().get(i);

                sDeptNames.appendLine(deptEntitiy.getName());
            }

            tvDept.setText(sDeptNames.create());
        }
        ImageLoader.displayImage(getContext(), userEntity.getAvatarUrl(), ivAvater);
    }

    @OnClick(R2.id.btn_logout)
    public void onViewClicked() {
        getPresenter().logout();
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }


    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener = item -> {
        int i = item.getItemId();
        if (i == R.id.my_info) {
            ARouter.getInstance().build(ARouterConstant.UserProfileActivity).navigation();
        } else if (i == R.id.change_password) {
           ChangePasswordActivity.start(mContext);
        } else if (i == R.id.message) {
            ARouter.getInstance().build(ARouterConstant.MsgSetActivity).navigation();
        } else if (i == R.id.about) {
            AboutActivity.start(mContext);

        } else if (i == R.id.other) {
            ShareApkActivity.start(mContext);
        }

        return true;
    };
}
