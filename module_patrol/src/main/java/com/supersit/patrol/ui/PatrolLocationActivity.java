package com.supersit.patrol.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.ui.ShowMediasActivity;
import com.supersit.common.utils.MyUtil;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = ARouterConstant.PatrolLocationActivity)
public class PatrolLocationActivity extends ToolbarActivity {

    @BindView(R2.id.mapView)
    public MapView mMapView;
    @BindView(R2.id.tv_upload_user)
    TextView tvUploadUser;
    @BindView(R2.id.tv_upload_time)
    TextView tvUploadTime;
    @BindView(R2.id.tv_affiliation_project)
    TextView tvAffiliationProject;
    @BindView(R2.id.tv_upload_address)
    TextView tvUploadAddress;
    @BindView(R2.id.tv_event_title)
    TextView tvEventTitle;
    @BindView(R2.id.tv_event_remark)
    TextView tvEventRemark;

    public EventEntity mEventEntity;

    private BaiduMap mBaiduMap;

    public static void start(Context context, EventEntity eventEntity) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, PatrolLocationActivity.class);
        bundle.putParcelable("eventEntity", eventEntity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    public void initData(Bundle bundle) {
        mEventEntity = bundle.getParcelable("eventEntity");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_patrol_location;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.patrol_dynamic);
        if (null != mEventEntity) {
            tvUploadUser.setText(mEventEntity.getUserEntity().getNickName());
            tvUploadTime.setText(TimeUtils.millis2String(mEventEntity.getTime()));
            tvAffiliationProject.setText(mEventEntity.getDeptEntitiy().getName());
            tvUploadAddress.setText(mEventEntity.getAddress());
            StringBuffer eventInfo = new StringBuffer(mEventEntity.getEventTitle()).append("(").append(mEventEntity.getEventType()).append(")");
            tvEventTitle.setText(eventInfo.toString());
            tvEventRemark.setText(mEventEntity.getEventRemark());
        }

        mBaiduMap = mMapView.getMap();
        initMapOption();
    }

    private void initMapOption() {
        // 开启定位图层
        mBaiduMap.setOnMapLoadedCallback(mOnMapLoadedCallback);
        mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);// 是否显示指南针;
        mUiSettings.setRotateGesturesEnabled(false);// 不允许旋转
        mUiSettings.setOverlookingGesturesEnabled(false);// 不允许俯视
    }

    private BaiduMap.OnMapLoadedCallback mOnMapLoadedCallback = () -> {
        if (null != mEventEntity) {
            LatLng latLng = new LatLng(mEventEntity.getLatitude(), mEventEntity.getLongitude());
            List<String>  mediaUrls = mEventEntity.getMediaUrls();
            if(null != mediaUrls && !mediaUrls.isEmpty()){
                String url = mediaUrls.get(0);
                if(Constant.IMAGE_CODE == MyUtil.getFileType(url)){
                    Glide.with(mContext).load(url).asBitmap().thumbnail(0.1f).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            showPicMarker(latLng,bitmap,mediaUrls);
                        }
                    });
                }else if(Constant.VEDIO_CODE == MyUtil.getFileType(url)){
                    showPicMarker(latLng,BitmapFactory.decodeResource(getResources(),R.mipmap.ic_video_nor),mediaUrls);
                }
            }else{
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.mipmap.ic_maker);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(latLng).icon(bitmap);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
            }
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(16.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

    };

    BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if(null != mEventEntity.getMediaUrls() && !mEventEntity.getMediaUrls().isEmpty()){
                ShowMediasActivity.start(mContext,mEventEntity.getEventTitle(),0,mEventEntity.getMediaUrls());
            }
            return true;
        }
    };

    public void showPicMarker(LatLng latLng, Bitmap bitmap,List<String> mediaUrls){
        View  v = LayoutInflater.from(mContext).inflate( R.layout.view_picture_marker, null);
        ImageView imageView= v.findViewById(R.id.iv_prcture);
        bitmap=Bitmap.createScaledBitmap(bitmap, 200,200,false);
        imageView.setImageBitmap(bitmap);
        TextView textView=(TextView) v.findViewById(R.id.tv_count);
        if(mediaUrls.size()>1){
            textView.setText(String.valueOf(mediaUrls.size()));
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.GONE);
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(v);
        MarkerOptions option = new MarkerOptions().position(latLng).icon(bitmapDescriptor).anchor(0.5f, 1.0f);
        // 在地图上添加Marker，并显示
        Marker marker = (Marker) mBaiduMap.addOverlay(option);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("key", (ArrayList<String>) mediaUrls);
        marker.setExtraInfo(bundle);
    }

    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (null != mBaiduMap)
            mMapView.onDestroy();
        super.onDestroy();
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
