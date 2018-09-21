package com.supersit.easeim.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.supersit.common.base.BaseFragment;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
@Route(path = "/im/BlankFragment")
public class BlankFragment extends BaseFragment {

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_blank;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @OnClick(R2.id.text)
    public void onViewClicked() {
//        ChatActivity.start(mContext);
    }
}
