package com.supersit.main.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.widget.edittext.AutoCheckEditText;
import com.supersit.common.widget.edittext.AutoCheckEditTextClass;
import com.supersit.common.widget.edittext.EditTextType;
import com.supersit.main.R;
import com.supersit.main.R2;
import com.supersit.main.presenter.ChangePasswordPresenter;
import com.supersit.mvp.presenter.RequirePresenter;

import butterknife.BindView;
import butterknife.OnClick;

@RequirePresenter(ChangePasswordPresenter.class)
public class ChangePasswordActivity extends ToolbarActivity<ChangePasswordPresenter> {

    @BindView(R2.id.til_old_password)
    TextInputLayout tilOldPassword;
    @BindView(R2.id.til_new_password)
    TextInputLayout tilNewPassword;
    @BindView(R2.id.til_new_password2)
    TextInputLayout tilNewPassword2;


    private AutoCheckEditTextClass mOldPasswordEditTextClass,mNewPasswordEditTextClass;

    public static void start(Context context){
        Intent intent=new Intent(context,ChangePasswordActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.change_password);
        mOldPasswordEditTextClass = new AutoCheckEditTextClass(tilOldPassword,(AutoCheckEditText) tilOldPassword.getEditText()).checkType(EditTextType.TYPE_OF_NOT_EMPTY);
        mNewPasswordEditTextClass = new AutoCheckEditTextClass(tilNewPassword,(AutoCheckEditText) tilNewPassword.getEditText()).checkType(EditTextType.TYPE_OF_NOT_EMPTY).setMinLength(3).setMaxLength(10);
        tilNewPassword2.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkNewPassword2();
            }
        });
    }

    @OnClick(R2.id.btn_confirm)
    public void onViewClicked() {
        changePassword();
    }

    private boolean checkNewPassword2(){
        String newPassword = tilNewPassword.getEditText().getText().toString();
        String newPassword2 = tilNewPassword2.getEditText().getText().toString();
        if(!newPassword.equals(newPassword2)){
            tilNewPassword2.setErrorEnabled(true);
            tilNewPassword2.setError("两次输入不一致~~");
            tilNewPassword2.requestFocus();
            return true;
        }else{
            tilNewPassword2.setErrorEnabled(false);
            tilNewPassword2.setError(null);
        }
        return false;
    }

    private void changePassword() {
        if(mOldPasswordEditTextClass.isException() || mNewPasswordEditTextClass.isException() || checkNewPassword2()){
            return;
        }
        final String oldPassword = tilOldPassword.getEditText().getText().toString().trim();
        final String newPassword = tilNewPassword.getEditText().getText().toString().trim();
        getPresenter().changePassword(oldPassword,newPassword);
    }

}
