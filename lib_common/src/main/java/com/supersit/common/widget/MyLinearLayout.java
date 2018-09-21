package com.supersit.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * 解决 toolbar + edittext + 输入法问题
 * Created by 田皓午 on 2018/4/10.
 */

public class MyLinearLayout  extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }
    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }

}
