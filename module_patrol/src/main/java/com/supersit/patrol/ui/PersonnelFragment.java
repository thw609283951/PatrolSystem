package com.supersit.patrol.ui;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.map.MapView;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.provider.IIMService;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.presenter.PersonnelPresenter;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = ARouterConstant.PersonnelFragment)
@RequirePresenter(PersonnelPresenter.class)
public class PersonnelFragment extends BaseFragment<PersonnelPresenter> {


    @BindView(R2.id.mapView)
    public MapView mMapView;
    @BindView(R2.id.sc_project_location)
    public SwitchCompat scProjectLocation;
    @BindView(R2.id.sc_personnel_location)
    public SwitchCompat scPersonnelLocation;

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_personnel;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        scProjectLocation.setOnCheckedChangeListener(mOnCheckedChangeListener);
        scPersonnelLocation.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }


    @OnClick({R2.id.btn_reset})
    public void onViewClicked(View view) {
        getPresenter().resetMap();
    }

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = (compoundButton, isChecked) -> {
        if(compoundButton.getId() == R.id.sc_project_location){
            if(isChecked){
                getPresenter().showProjectsLocation();
            }else{
                getPresenter().hideProjectsLocation();
            }
        } else if(compoundButton.getId() == R.id.sc_personnel_location){
            if(isChecked){
                getPresenter().showPersonnelMarkers();
            }else{
                getPresenter().hidePersonnelMarkers();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
