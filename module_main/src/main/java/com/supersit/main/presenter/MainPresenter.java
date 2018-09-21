package com.supersit.main.presenter;


import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.RxBusEntity;
import com.supersit.common.provider.IIMService;
import com.supersit.main.R;
import com.supersit.common.entity.TabEntity;
import com.supersit.main.ui.MainActivity;
import com.supersit.main.ui.SettingsFragment;
import com.supersit.main.ui.StatisticAnalysisFragment;
import com.supersit.main.ui.TestFragment;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */

public class MainPresenter extends BasePresenter<MainActivity> {


    private ArrayList<CustomTabEntity> mTabEntities;
    private List<Fragment> mMainFragments;


    @Autowired(name = ARouterConstant.IIMService)
    public IIMService mIIMService;

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().inject(this);
        initData();
    }

    private void initData(){
        mTabEntities = new ArrayList<>();
        mMainFragments = new ArrayList<>();
        for (int i = 0; i < getView().mTabTitles.length; i++) {
            mTabEntities.add(new TabEntity(getView().mTabTitles[i], getView().mTabPressedIcons[i], getView().mTabNormalIcons[i]));
            mMainFragments.add(getFragmentById(getView().mTabTitles[i]));
        }
        getView().initFragments(mMainFragments, mTabEntities);
        updateUnreadLabel();
    }

    public Fragment getFragmentById(String title){
        if(title.equals(getView().getString(R.string.instant_messaging))){
            return (Fragment) ARouter.getInstance().build(ARouterConstant.IMFragment).navigation();
        }else if(title.equals(getView().getString(R.string.patrol_inspection))){
            return (Fragment) ARouter.getInstance().build(ARouterConstant.PatrolFragment).navigation();
        }else if(title.equals(getView().getString(R.string.routine_work))){
            return (Fragment) ARouter.getInstance().build(ARouterConstant.WorkFragment).navigation();
        }else if(title.equals(getView().getString(R.string.personnel_loction))){
            return (Fragment) ARouter.getInstance().build(ARouterConstant.PersonnelFragment).navigation();
        }else if(title.equals(getView().getString(R.string.statistic_analysis))){
            return new StatisticAnalysisFragment();
        } else if(title.equals(getView().getString(R.string.my_info))){
            return new SettingsFragment();
        }
        return new TestFragment();
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void recUpUnReadIMMsgEvent(RxBusEntity eventBusEntity) {
        if(Constant.RXBUS_UPIM_UNREAD_COUNT == eventBusEntity.getId()){
            updateUnreadLabel();
        }
    }

    public void updateUnreadLabel() {
        int count = mIIMService.getUnreadIMMsgCountTotal();
        getView().updateUnreadLabel(count);
    }

}
