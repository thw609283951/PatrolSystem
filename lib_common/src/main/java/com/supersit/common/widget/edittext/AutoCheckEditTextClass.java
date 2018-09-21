package com.supersit.common.widget.edittext;

import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;

/**
 * Created by Administrator on 2017/11/22.
 */

public class AutoCheckEditTextClass implements WarnViewStatus {
    private static final String TAG = "AutoCheckEditTextClass";

    private TextInputLayout mTextInputLayout;
    private AutoCheckEditText mAutoCheckEditText;

    /**
     * 传入相关联的TextInputLayout与AutoCheckEditText
     *
     * @param editText
     */
    public AutoCheckEditTextClass(AutoCheckEditText editText) {
        this(null,editText);
    }

    public AutoCheckEditTextClass(TextInputLayout textInputLayout,AutoCheckEditText editText) {
        mAutoCheckEditText = editText;
        mTextInputLayout = textInputLayout;
    }


    /**
     * 设置正则校验类型
     *
     * @param typ
     * @return
     */
    public AutoCheckEditTextClass checkType(int typ) {
        mAutoCheckEditText.creatCheck(typ, this);
        return this;
    }

    /**
     * 设置最小判断长度(一般不设置,默认0)
     *
     * @param minLength
     * @return
     */
    public AutoCheckEditTextClass setMinLength(int minLength) {
        mAutoCheckEditText.setMinLength(minLength);
        return this;
    }

    /**
     * @param maxLength      设置最大长度的时候,一并设置计算器的最大字数限制
     * @param counterEnabled 计算器是否开启
     * @return
     */
    public AutoCheckEditTextClass setMaxLength(int maxLength, boolean counterEnabled) {
        if(null != mTextInputLayout){
            mTextInputLayout.setCounterMaxLength(maxLength);
            mTextInputLayout.setCounterEnabled(counterEnabled);
        }

        mAutoCheckEditText.setMaxLength(maxLength);
        return this;
    }

    /**
     * @param maxLength 设置最大长度的时候,一并设置计算器的最大字数限制
     * @return
     */
    public AutoCheckEditTextClass setMaxLength(int maxLength) {
        if(null != mTextInputLayout){
            mTextInputLayout.setCounterMaxLength(maxLength);
            mTextInputLayout.setCounterEnabled(false);
        }

        mAutoCheckEditText.setMaxLength(maxLength);
        return this;
    }

    /**
     * TextInputLayout hint开关
     * 如果只想用EditText默认效果的话,请传false,默认是true
     *
     * @param hintEnabled
     * @return
     */
    public AutoCheckEditTextClass setHintEnabled(boolean hintEnabled) {
        if(null != mTextInputLayout)
            mTextInputLayout.setHintEnabled(hintEnabled);
        return this;
    }

    @Override
    public boolean isException() {
        mAutoCheckEditText.check();

        if(null != mTextInputLayout && null!=mTextInputLayout.getError()){
            mTextInputLayout.requestFocus();
            return true;
        }
        if(null!=mAutoCheckEditText.getError()){
            mAutoCheckEditText.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    public void show(String... msgs) {
        if(null != mTextInputLayout)
            mTextInputLayout.setErrorEnabled(true);
        if (msgs.length > 0 && !StringUtils.isEmpty(msgs[0])) {
            if(null != mTextInputLayout)
                mTextInputLayout.setError(msgs[0]);
            else
                mAutoCheckEditText.setError(msgs[0]);
        }
    }

    @Override
    public void hide() {
        if(null != mTextInputLayout){
            mTextInputLayout.setError(null);
            mTextInputLayout.setErrorEnabled(false);
        }else{
            mAutoCheckEditText.setError(null);
        }
    }
}