package com.supersit.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.widget.recyclerview.DividerDecoration;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 位置纠正页
 * Created by Administrator on 2017/11/7.
 */

public class LocationCorrectionActivity extends ToolbarActivity {
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.mapview)
    MapView mMapView;
    @BindView(R2.id.tv_lng)
    TextView tvLng;
    @BindView(R2.id.tv_lat)
    TextView tvLat;
    @BindView(R2.id.ib_again_location)
    ImageButton ibAgainLocation;
    @BindView(R2.id.rv_poi)
    RecyclerView rvPoi;

    private BaiduMap mBaiduMap;

    //poi搜索
    public PoiSearch mPoiSearch;

//    private PoiRadioAdapter mAdapter;
    private LatLng mCurrLatLng;
    private String address;

    private LocationClient mLocClient;
    // 当前是否为 点选 poi列表
    private boolean isSelectedPoiItem = false;


    public static void start(Activity context){
        Intent intent=new Intent(context,LocationCorrectionActivity.class);
        Bundle bundle=new Bundle();
        intent.putExtras(bundle);
        context.startActivityForResult(intent, Constant.CODE_LOCATION_REQUEST);
    }


    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_loction_correction;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.loction_correction);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvPoi.setLayoutManager(linearLayoutManager);
        rvPoi.addItemDecoration(new DividerDecoration(mContext, LinearLayoutManager.VERTICAL));
        doBusiness();
    }

    public void doBusiness() {

        if (mBaiduMap == null) {
            mBaiduMap = mMapView.getMap();
        }
        //设置默认定位按钮是否显示，非必需设置。
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);// 是否显示指南针;
        mUiSettings.setRotateGesturesEnabled(false);// 不允许旋转
        mUiSettings.setOverlookingGesturesEnabled(false);// 不允许俯视


        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setOpenAutoNotifyMode();
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    @OnClick(R2.id.ib_again_location)
    public void onViewClicked() {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(mCurrLatLng).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * 菜单项被点击时调用，也就是菜单项的监听方法。
         * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。 TODO Auto-generated
         * method stub
         */
        int i = item.getItemId();
        if (i == R.id.confirm) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", mCurrLatLng.latitude);
            bundle.putDouble("longitude", mCurrLatLng.longitude);
            bundle.putString("address", address);
            intent.putExtras(bundle);

            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            mCurrLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            address=location.getAddrStr();
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(mCurrLatLng).zoom(16.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        if(null != mBaiduMap)
            mMapView.onDestroy();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        mlocationClient.unRegisterLocationListener(mAMapLocationListener);
//        mlocationClient.stopLocation();


    }

}
