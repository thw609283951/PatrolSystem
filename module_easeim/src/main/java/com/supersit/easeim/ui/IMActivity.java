package com.supersit.easeim.ui;

import android.os.Bundle;
import android.view.View;

import com.supersit.common.base.BaseActivity;
import com.supersit.easeim.R;




//

public class IMActivity extends BaseActivity {

    public void initView() {

    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_im;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        IMFragment imFragment = new IMFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_im,imFragment).commit();
    }
}
