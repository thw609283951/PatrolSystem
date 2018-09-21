package com.supersit.easeim.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hyphenate.easeui.widget.EaseImageView;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.utils.ActionUtil;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.SpannableStringUtil;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = ARouterConstant.UserProfileActivity)
public class UserProfileActivity extends ToolbarActivity {


    @BindView(R2.id.iv_avatar)
    EaseImageView ivAvatar;
    @BindView(R2.id.tv_nickname)
    TextView tvNickname;
    @BindView(R2.id.tv_username)
    TextView tvUsername;
    @BindView(R2.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R2.id.tv_positions)
    TextView tvPositions;
    @BindView(R2.id.tv_dept)
    TextView tvDept;
    @BindView(R2.id.btn_send_msg)
    Button btnSendMsg;

    private UserEntity mUserEntity;

    public static void start(Activity mContext, String userName) {
        Bundle bundle = new Bundle();
        bundle.putString("username", userName);
        Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        setTitle(R.string.user_profile);
        String username = getIntent().getStringExtra("username");
        if (null == username || username.equals(UserModel.getInstance().getCurrUserName()))
            mUserEntity = UserModel.getInstance().getCurrUser();
        else
            mUserEntity = UserModel.getInstance().getUserByUserName(username);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_user_profile;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        findViewById(R.id.ll_root).setFitsSystemWindows(true);
        if (null != mUserEntity) {
            tvUsername.setText(mUserEntity.getUserName());
            tvNickname.setText(mUserEntity.getNickName());
            tvPhoneNumber.setText(mUserEntity.getModePhone());
            tvPositions.setText(mUserEntity.getPositions());
            if (null != mUserEntity.getDepts() && !mUserEntity.getDepts().isEmpty()) {
                SpannableStringUtil sDeptNames = new SpannableStringUtil();
                for (DeptEntity deptEntitiy : mUserEntity.getDepts()) {
                    sDeptNames.appendLine(deptEntitiy.getName());
                }
                tvDept.setText(sDeptNames.create());
            }
            ImageLoader.displayImage(mContext, mUserEntity.getAvatarUrl(), R.drawable.ic_default_avatar, ivAvatar);
            btnSendMsg.setVisibility(mUserEntity.getUserName().equals(UserModel.getInstance().getCurrUserName()) ? View.GONE : View.VISIBLE);
        } else {
            showErrorToast("找不到该用户~~");
        }

    }

    @OnClick({R2.id.btn_send_msg,R2.id.tv_phone_number})
    public void onViewClicked(View view) {
        int i = view.getId();
        if(i == R.id.btn_send_msg){
            ChatActivity.start((Activity) mContext, mUserEntity.getUserName());
        }else if(i == R.id.tv_phone_number){
            String modePhone= mUserEntity.getModePhone();
            //用intent启动拨打电话
            if (null != modePhone )
                ActionUtil.call(mContext,modePhone);
        }
    }

}
