package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.supersit.common.ARouterConstant;
import com.supersit.common.adapter.GridUserAdapter;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.DeptLocationEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.supersit.patrol.ui.PatrolRecordActivity.DEPT_CODE;

/**
 * Created by 田皓午 on 2018/4/22.
 */

public class ProjectInfoActivity extends ToolbarActivity {

    @BindView(R2.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_remark)
    TextView tvRemark;
    @BindView(R2.id.tv_member_number)
    TextView tvMemberNumber;
    @BindView(R2.id.tv_project_location)
    TextView tvAddress;
    @BindView(R2.id.rv_users)
    RecyclerView rvUsers;
    @BindView(R2.id.mapView)
    MapView mapView;
    private BaiduMap mBaiduMap;
    private DeptEntity mDeptEntitiy;
    private int mSpanCount = 5;

    public static void start(Activity mContext, DeptEntity deptEntitiy) {
        Intent intent = new Intent(mContext, ProjectInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("deptEntitiy", deptEntitiy);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        mDeptEntitiy = bundle.getParcelable("deptEntitiy");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_project_info;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.project_info);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, mSpanCount);
        rvUsers.setLayoutManager(gridLayoutManager);
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_m);
        rvUsers.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, spacingInPixels, false));

        tvName.setText(mDeptEntitiy.getName());
        tvRemark.setText(null == mDeptEntitiy.getRemark() ? "详情" : mDeptEntitiy.getRemark());
        DeptLocationEntity deptLocationEntity = mDeptEntitiy.getDeptLocationEntity();
        tvAddress.setText(null == deptLocationEntity? "未设" : deptLocationEntity.getAddress());
        List<UserEntity> userEntities = mDeptEntitiy.getUserEntitys();

        int memberNumber = 0;
        if(null != userEntities){
            memberNumber = userEntities.size();
            List<UserEntity> tempUserEntitys = new ArrayList<>();
            if(memberNumber>5)
                tempUserEntitys = userEntities.subList(1,5);
            else
                tempUserEntitys = userEntities;

            GridUserAdapter mAdapter = new GridUserAdapter(tempUserEntitys);
            mAdapter.setOnItemClickListener((adapter, view1, position) -> {
                UserEntity userEntity = (UserEntity) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("username", userEntity.getUserName());
                ARouter.getInstance().build(ARouterConstant.UserProfileActivity).with(bundle).navigation();
            });
            mAdapter.bindToRecyclerView(rvUsers);
        }
        tvMemberNumber.setText(getString(R.string.project_member_number,memberNumber));
        mBaiduMap = mapView.getMap();
        initMapOption();
    }

    private void initMapOption(){
        // 开启定位图层
        mBaiduMap.setOnMapLoadedCallback(onMapLoadedCallback);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration( MyLocationConfiguration.LocationMode.FOLLOWING, true,null));
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);// 是否显示指南针;
        mUiSettings.setRotateGesturesEnabled(false);// 不允许旋转
        mUiSettings.setOverlookingGesturesEnabled(false);// 不允许俯视
    }

    private BaiduMap.OnMapLoadedCallback onMapLoadedCallback = () -> {
        DeptLocationEntity deptLocationEntity = mDeptEntitiy.getDeptLocationEntity();
        if(null != deptLocationEntity){
            LatLng latLng = new LatLng(deptLocationEntity.getLongitude(),deptLocationEntity.getLatitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_marker_construction);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(latLng).icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(16.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

    };

    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if(null != mBaiduMap)
            mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @OnClick({R2.id.rl_patrol_record, R2.id.rl_project_member})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.rl_project_member) {
            if(null != mDeptEntitiy.getUserEntitys() && !mDeptEntitiy.getUserEntitys().isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("userEntitys", (ArrayList<? extends Parcelable>) mDeptEntitiy.getUserEntitys());
                ARouter.getInstance().build(ARouterConstant.ContactListActivity).with(bundle).navigation();
            }
        } else if (i == R.id.rl_patrol_record) {
            PatrolRecordActivity.start((Activity) mContext,PatrolRecordActivity.DEPT_CODE,mDeptEntitiy.getId());
        }
    }
}
