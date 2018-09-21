package com.supersit.easeim.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hyphenate.chat.EMClient;
import com.supersit.common.ARouterConstant;
import com.supersit.common.adapter.FragmentAdapter;
import com.supersit.common.base.BaseFragment;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.presenter.IMPresenter;
import com.supersit.mvp.presenter.RequirePresenter;
import butterknife.BindArray;
import butterknife.BindView;


@Route(path = ARouterConstant.IMFragment)
@RequirePresenter(IMPresenter.class)
public class IMFragment extends BaseFragment<IMPresenter>{

    @BindArray(R2.array.im_tabs)
    public String[] mIMTabs;

    @BindView(R2.id.tab_im)
    SegmentTabLayout tabIm;
    @BindView(R2.id.vp_im)
    ViewPager vpIm;

    private FragmentAdapter mViewPagerAdapter;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_im;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        tabIm.setTabData(mIMTabs);
        mViewPagerAdapter = new FragmentAdapter(getChildFragmentManager(), getPresenter().getFragments());
        vpIm.setOffscreenPageLimit(3);
        vpIm.setAdapter(mViewPagerAdapter);

        tabIm.setOnTabSelectListener(mOnTabSelectListener);
        vpIm.addOnPageChangeListener(mOnPageChangeListener);
        vpIm.setCurrentItem(0);
    }


    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    private OnTabSelectListener mOnTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            vpIm.setCurrentItem(position);
        }

        @Override
        public void onTabReselect(int position) {

        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tabIm.setCurrentTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public void updateUnreadLabel(){
        int count = getUnreadMsgCountTotal();
        if(count > 0 ){
            tabIm.showDot(0);
        }else{
            tabIm.hideMsg(0);
        }
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }
}
