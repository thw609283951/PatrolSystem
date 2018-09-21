package com.supersit.common.widget.edittext;

/**
 * Created by Administrator on 2017/11/22.
 */

public interface WarnViewStatus {
    /**
     * 展示警告语
     *
     * @param msgs
     */
    void show(String... msgs);

    /**
     * 隐藏警告语
     */
    void hide();

    boolean isException();
}