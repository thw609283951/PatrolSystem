package com.supersit.patrol.presenter;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.DeptLocationEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.provider.IIMService;
import com.supersit.patrol.R;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.ui.PatrolFragment;
import com.supersit.patrol.ui.ProjectInfoActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Administrator on 2018/3/21.
 */

public class PatrolPresenter extends BasePresenter<PatrolFragment> {

    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;

    private Double lastX = 0.0;
    private int mCurrentDirection = 0;


    private float mCurrentAccracy;
    boolean isFirstLoc = true; // 是否首次定位

    private LatLng mCenterLatLng,mCurrLatLng;

    private SensorManager mSensorManager;

    private List<DeptEntity> mProjects;
    private List<Overlay> mProjectLocationMarkers;

    @Autowired(name = ARouterConstant.IIMService)
    public IIMService mIIMService;

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().inject(this);

        mBaiduMap = getView().mMapView.getMap();
        mSensorManager = (SensorManager) getView().getActivity().getSystemService(SENSOR_SERVICE);//获取传感器管理服务
    }

    public void initMap(){
        initMapOption();
    }

    private void initMapOption(){
        // 开启定位图层
        mBaiduMap.setOnMapLoadedCallback(onMapLoadedCallback);
        mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration( MyLocationConfiguration.LocationMode.NORMAL, true,null));
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);// 是否显示指南针;
        mUiSettings.setRotateGesturesEnabled(false);// 不允许旋转
        mUiSettings.setOverlookingGesturesEnabled(false);// 不允许俯视
        // 定位初始化
        mLocClient = new LocationClient(getView().getContext());
        mLocClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setOpenAutoNotifyMode();
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    public void getProjects(){
        PatrolModel.getInstance().getProjectList(UserModel.getInstance().getCurrUserId())
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(disposable -> putDisposable(disposable))
                .subscribe(new Observer<MyResponse<List<DeptEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getView().showProgressDialog(R.string.data_loading,d);
                    }

                    @Override
                    public void onNext(MyResponse<List<DeptEntity>> value) {
                        mProjects = value.getResultData();
                        if(null == mProjects)
                            mProjects = new ArrayList<>();

                        showProjectsLocation();

                        int count = mProjects.size();
                        getView().showSuccessToast("共查询"+count+"个工程");
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showErrorToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        getView().dismissProgressDialog();
                    }
                });
    }

    public void showProjectsLocation(){
        if(null == mProjects){
            getProjects();
            return;
        }

        if(null == mProjectLocationMarkers){
            mProjectLocationMarkers = new ArrayList<>();
            if(!mProjects.isEmpty()){
                List<OverlayOptions> overlayOptions = new ArrayList<>();
                for (DeptEntity deptEntitiy : mProjects) {
                    DeptLocationEntity deptLocationEntity = deptEntitiy.getDeptLocationEntity();
                    if(null != deptLocationEntity){
                        LatLng point = new LatLng(deptLocationEntity.getLongitude(), deptLocationEntity.getLatitude());
                        //构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_construction);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("deptEntitiy",deptEntitiy);

                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap)
                                .title(deptEntitiy.getName()).extraInfo(bundle);
                        overlayOptions.add(option);
                    }
                }
                mProjectLocationMarkers = mBaiduMap.addOverlays(overlayOptions);
            }
        }else{
            for (Overlay overlay : mProjectLocationMarkers) {
                overlay.setVisible(true);
            }
        }
    }

    public void hideProjectsLocation(){
        if(null != mProjectLocationMarkers){
            for (Overlay overlay : mProjectLocationMarkers) {
                overlay.setVisible(false);
            }
        }

    }

    private BaiduMap.OnMapLoadedCallback onMapLoadedCallback = () -> {
       DistrictSearch mDistrictSearch = DistrictSearch.newInstance();
        DistrictSearchOption option = new DistrictSearchOption();
        option.cityName("广州");
        mDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<List<LatLng>> pointsList = districtResult.getPolylines();
                    if (pointsList == null && pointsList.size()==0) return;
                    List<LatLng> scopeLatLngs=pointsList.get(0);
                    LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();;
                    for (LatLng ll : scopeLatLngs) {
                        latLngBounds.include(ll);
                    }
                    // 构建用户绘制多边形的Option对象
                    OverlayOptions polygonOption = new PolygonOptions().points(scopeLatLngs)
                            .stroke(new Stroke(5, Color.argb(255, 255, 0, 0)))
                            .fillColor(0x00000000);
                    // 在地图上添加多边形Option，用于显示
                    mBaiduMap.addOverlay(polygonOption);

                    mCenterLatLng = latLngBounds.build().getCenter();
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(mCenterLatLng).zoom(11f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        });
        mDistrictSearch.searchDistrict(option);//执行行政区域的搜索
    };

    private BaiduMap.OnMarkerClickListener mOnMarkerClickListener = marker -> {
        Bundle bundle = marker.getExtraInfo();
        if(null !=bundle){
            DeptEntity deptEntitiy = bundle.getParcelable("deptEntitiy");
            ProjectInfoActivity.start(getView().getActivity(),deptEntitiy);
        }
        return true;
    };

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || getView().mMapView == null) {
                return;
            }

            mCurrLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mCurrentAccracy = location.getRadius();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mCurrLatLng).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    };

    public void resetMap(){
        MapStatus.Builder builder = new MapStatus.Builder();
        if(mCenterLatLng != null && getView().scProjectLocation.isChecked() || getView().scProjectLocation.isChecked()){
            builder.target(mCenterLatLng).zoom(9.5f);
        } else if(null != mCurrLatLng){
            builder.target(mCurrLatLng).zoom(16.0f);
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double x = sensorEvent.values[SensorManager.DATA_X];

            if (null != mCurrLatLng && Math.abs(x - lastX) > 1.0) {
                mCurrentDirection = (int) x;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccracy)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentDirection).latitude(mCurrLatLng.latitude)
                        .longitude(mCurrLatLng.longitude).build();
                mBaiduMap.setMyLocationData(locData);
            }
            lastX = x;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onDestroy();
    }
}
