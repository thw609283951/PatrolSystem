package com.supersit.common.utils;

import android.widget.EditText;

import com.supersit.common.R;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class ViewUtils {

    public static boolean hasEmptyOfEditText(EditText view) {
        String msg = view.getEditableText().toString().trim();
        if (msg.isEmpty()) {
            view.setError(view.getContext().getResources().getString(R.string.cannot_be_empty));
            view.requestFocus();

            return true;
        }else{
            if(null != view.getError())
                view.setError(null);
            return false;
        }
    }
}
