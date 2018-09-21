/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supersit.main.ui;

import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseActivity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.provider.IIMService;
import com.supersit.common.utils.PermissionUtils;
import com.supersit.common.widget.edittext.AutoCheckEditText;
import com.supersit.common.widget.edittext.AutoCheckEditTextClass;
import com.supersit.common.widget.edittext.EditTextType;
import com.supersit.main.R;
import com.supersit.main.R2;
import com.supersit.main.presenter.LoginPresenter;
import com.supersit.mvp.presenter.RequirePresenter;


import butterknife.BindView;

/**
 * Login screen
 */
@RequirePresenter(LoginPresenter.class)
public class LoginActivity extends BaseActivity<LoginPresenter> {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R2.id.til_username)
    TextInputLayout name;
    @BindView(R2.id.til_password)
    TextInputLayout password;

    private AutoCheckEditTextClass mNameEditTextClass,mPasswordEditTextClass;


    public static void start(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        // enter the main activity if already logged in
        ARouter.getInstance().inject(this);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        PermissionUtils.requestPermissions(mContext);
        mNameEditTextClass = new AutoCheckEditTextClass(name,(AutoCheckEditText) name.getEditText())
                .checkType(EditTextType.TYPE_OF_NOT_EMPTY);

        mPasswordEditTextClass = new AutoCheckEditTextClass(password,
                (AutoCheckEditText) password.getEditText())
                .checkType(EditTextType.TYPE_OF_NOT_EMPTY);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * login
     *
     * @param view
     */
    public void login(View view) {
        if (mNameEditTextClass.isException() || mPasswordEditTextClass.isException())
            return;
        String currentUsername = name.getEditText().getText().toString().trim();
        String currentPassword = password.getEditText().getText().toString().trim();
        getPresenter().login(currentUsername,currentPassword);
    }
}
