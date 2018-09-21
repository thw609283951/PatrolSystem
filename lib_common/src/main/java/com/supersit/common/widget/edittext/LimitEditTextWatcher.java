package com.supersit.common.widget.edittext;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by 田皓午 on 2018/5/16.
 */

public class LimitEditTextWatcher implements TextWatcher {

    private EditText mEditText = null;
    private TextInputLayout mTextInputLayout = null;

    public LimitEditTextWatcher(EditText editText) {
        mEditText = editText;
        View view = (View) editText.getParent();
        if(view instanceof TextInputLayout)
            mTextInputLayout = (TextInputLayout) view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
