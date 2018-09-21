package com.supersit.patrol.ui;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.mapapi.map.MapView;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.ui.LocationCorrectionActivity;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.presenter.PatrolPresenter;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.baidu.location.BDLocation.GPS_ACCURACY_BAD;
import static com.baidu.location.BDLocation.GPS_ACCURACY_GOOD;
import static com.baidu.location.BDLocation.GPS_ACCURACY_MID;

@Route(path = ARouterConstant.PatrolFragment)
@RequirePresenter(PatrolPresenter.class)
public class PatrolFragment extends BaseFragment<PatrolPresenter> {

    @BindView(R2.id.mapView)
    public MapView mMapView;
    @BindView(R2.id.sc_project_location)
    public SwitchCompat scProjectLocation;
    @BindView(R2.id.sc_danger_location)
    public SwitchCompat scDangerLocation;
    @BindView(R2.id.tv_wait_up_count)
    public TextView tvWaitUpCount;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_patrol;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        scProjectLocation.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {
        AndPermission.with(mContext)
                .runtime()
                .permission(
                        Permission.Group.LOCATION,
                        Permission.Group.SENSORS)
                .onGranted(data -> {
                    getPresenter().initMap();
                }).onDenied(data -> {
                    Toasty.error(mContext, mContext.getString(R.string.lack_of_permissions)).show();
                }).start();
    }


    @OnClick({R2.id.btn_reset,R2.id.btn_event_up, R2.id.btn_wait_up_record, R2.id.btn_history_record})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_event_up) {
            PatrolUploadActivity.start(mContext);
        } else if (i == R.id.btn_wait_up_record) {
            PatrolUploadItemsActivity.start(mContext);
        } else if (i == R.id.btn_history_record) {
            PatrolRecordActivity.start(mContext);
        } else if(i == R.id.btn_reset){
            getPresenter().resetMap();
        }
    }

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = (compoundButton, isChecked) -> {
        if(compoundButton.getId() == R.id.sc_project_location){
            if(isChecked){
                getPresenter().showProjectsLocation();
            }else{
                getPresenter().hideProjectsLocation();
            }
        } else if(compoundButton.getId() == R.id.sc_danger_location){

        }
    };

    public void refreshWaitUpCount(){
        int count= PatrolModel.getInstance().getEventEntitysSize();
        if(count > 0){
            if(tvWaitUpCount.getVisibility() != View.VISIBLE)
                tvWaitUpCount.setVisibility(View.VISIBLE);
            tvWaitUpCount.setText(String.valueOf(count));
        }else{
            if(tvWaitUpCount.getVisibility() != View.INVISIBLE)
                tvWaitUpCount.setVisibility(View.INVISIBLE);
        }
    }


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

        refreshWaitUpCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
