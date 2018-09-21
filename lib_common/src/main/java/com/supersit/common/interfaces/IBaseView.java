package com.supersit.common.interfaces;

import android.os.Bundle;
import android.view.View;

/**
 * Created by Administrator on 2017/10/18.
 */

public interface IBaseView {

    /**
     * 初始化数据
     *
     * @param bundle 传递过来的bundle
     */
    void initData(final Bundle bundle);

    /**
     * 绑定布局
     *
     * @return 布局Id
     */
    int bindLayout();

    /**
     * 初始化view
     */
    void initView(final Bundle savedInstanceState, final View view);

}
