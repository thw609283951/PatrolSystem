package com.supersit.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.supersit.common.R;

/**
 * Created by 田皓午 on 2018/5/9.
 * 添加点击窗口事件监听
 */

public class BaseDialog extends Dialog{



    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public OnTouchOutsideListener mOnTouchOutsideListener;

    public interface OnTouchOutsideListener {
        void onTouchOutside();
    }

    public void setOnTouchOutsideListener(OnTouchOutsideListener onTouchOutsideListener){
        mOnTouchOutsideListener = onTouchOutsideListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* 触摸外部弹窗 */
        if (isOutOfBounds(getContext(), event)) {
//            onTouchOutside();
            if(null != mOnTouchOutsideListener)
                mOnTouchOutsideListener.onTouchOutside();
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }
}

