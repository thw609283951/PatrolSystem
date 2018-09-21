package com.supersit.patrol.model;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.EventEntity_Table;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.utils.RxUtils;
import com.supersit.mvp.model.SuperModel;
import com.supersit.patrol.R;
import com.supersit.patrol.db.PatrolDatabase;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.entity.EventTypeEntity;
import com.supersit.patrol.entity.LocationEntity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 田皓午 on 2018/4/1.
 */

public class PatrolModel extends SuperModel{
    private static final String TAG = PatrolModel.class.getSimpleName();

    private static String SHARED_KEY_BASE_EVENT_SYNC_LASTTIME = "SHARED_KEY_BASE_EVENT_SYNC_LASTTIME";

    private Boolean mIsBaseEventsSynced = null;
    private Long mSycnBaseEventsTimeMillis = null;

    private List<EventTypeEntity> mEventTypeEntities;

    public PatrolModel() {
    }

    @Override
    public void reset() {
        setBaseEventsSynced(false);

        mEventTypeEntities = null;
    }

    public static PatrolModel getInstance() {
        return getInstance(PatrolModel.class);
    }

    public void setBaseEventsSynced(boolean synced){
        mIsBaseEventsSynced = synced;
    }

    public Boolean isBaseEventsSynced(){
        if(null == mIsBaseEventsSynced)
            mIsBaseEventsSynced = false;
        return mIsBaseEventsSynced;
    }
    public void setLastSycnBaseEventsTimeMillis(long timeMillis){
        mSycnBaseEventsTimeMillis = timeMillis;
        putLong(SHARED_KEY_BASE_EVENT_SYNC_LASTTIME,timeMillis);
    }

    public long getLastSycnBaseEventsTimeMillis(){
        if(null == mSycnBaseEventsTimeMillis)
            mSycnBaseEventsTimeMillis = getLong(SHARED_KEY_BASE_EVENT_SYNC_LASTTIME,-1);
        return mSycnBaseEventsTimeMillis;
    }


    public Observable<MyResponse<List<EventTypeEntity>>> getBaseEvenst(){
        Type type = new TypeToken<MyResponse<List<EventTypeEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        return RxUtils.request(HttpMethod.GET, Urls.URL_EVENTS_BASE, type,params);
    }

    public void saveEventTypeEntitys(boolean isNewData,List<EventTypeEntity> eventTypeEntities){
        if(isNewData)
            Delete.table(EventTypeEntity.class);
        FlowManager.getDatabase(PatrolDatabase.class)
                .executeTransaction(new ProcessModelTransaction.Builder<EventTypeEntity>(
                        BaseModel::save
                ).addAll(eventTypeEntities).build());
    }

    public List<EventTypeEntity> getEventTypeEntitys(){
        if(null == mEventTypeEntities)
            mEventTypeEntities = SQLite.select().from(EventTypeEntity.class).queryList();
        return mEventTypeEntities;
    }


    //获取当前位置
    public Observable<MyResponse<LocationEntity>> getCurrLocationInfo(){
        return  Observable.create(emitter -> {
            MyResponse<LocationEntity> myResponse = new MyResponse<>();

            LocationClient locationClient = new LocationClient(Utils.getApp());
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            option.setIsNeedAddress(true);
            option.setIsNeedLocationDescribe(true);
            if(null != locationClient){
                locationClient.setLocOption(option);
                //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                locationClient.stop();
                locationClient.start();
                locationClient.registerLocationListener(new BDAbstractLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation location) {
                        LogUtils.i(TAG,"定位完成："+ location==null? "失败": location.getTime());
                        if(null != location && location.getLocType() ==61 || location.getLocType() == 161 ||
                                location.getLocType() == 66){
                            LocationEntity locationInfo = new LocationEntity();
                            locationInfo.setLatitude(location.getLatitude());
                            locationInfo.setLongitude(location.getLongitude());
                            if(null != location.getAddrStr()) {
                                StringBuffer sbAddress = new StringBuffer();
                                sbAddress.append(location.getCity());
                                sbAddress.append(location.getDistrict());
                                sbAddress.append(location.getStreet());
                                sbAddress.append(location.getStreetNumber());
                                if(null != location.getLocationDescribe())
                                    sbAddress.append("("+location.getLocationDescribe()+")");
                                locationInfo.setAddress(sbAddress.toString());
                            }
                            myResponse.setSuccess(true);
                            myResponse.setMsg(Utils.getApp().getString(R.string.load_success));
                            myResponse.setResultData(locationInfo);
                        }else{
                            myResponse.setSuccess(false);
                            myResponse.setMsg("定位失败，错误码"+location.getLocType());
                        }
                        emitter.onNext(myResponse);
                        emitter.onComplete();
                    }
                });
            }else{
                emitter.onError(new Throwable(Utils.getApp().getString(R.string.load_fail)));
                emitter.onComplete();


            }
        });
    }
    //上传事件信息
    public Observable<MyResponse> upLoadPatrolEvent(EventEntity eventEntity, List<File> files){
        Type type = new TypeToken<MyResponse>() {}.getType();
        HttpParams params = new HttpParams();
        params.put("userId",eventEntity.getUserId());
        params.put("receiveUserId",eventEntity.getReceiveUserId());
        params.put("deptId",eventEntity.getDeptId());
        params.put("workpastType",eventEntity.getEventType());
        params.put("workpastTitle",eventEntity.getEventTitle());
        params.put("workpastIsNormal",eventEntity.getNormal());
        params.put("workpastRemark",eventEntity.getEventRemark());
        params.put("workpastTime",eventEntity.getTime());
        params.put("workpastGpsX",eventEntity.getLatitude());
        params.put("workpastGpsY",eventEntity.getLongitude());
        params.put("workpastAddress",eventEntity.getAddress());
        params.putFileParams("file",files);

        return RxUtils.request(HttpMethod.POST, Urls.URL_UPLOAD_EVENT, type,params);
    }

    //获取历史上传记录
    public Observable<MyResponse<List<EventEntity>>> getPatrolRecord(int form,long userId,long lastTimeMillis ,String msg){
        Type type = new TypeToken<MyResponse<List<EventEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        String sForm = "user/";
        if(1 == form){
            sForm = "dept/";
        }
        if(0 == lastTimeMillis){
            lastTimeMillis = System.currentTimeMillis();
        }
        if(null == msg || msg.trim().isEmpty()){
            msg = "-1";
        }

        StringBuffer sBuffer = new StringBuffer( Urls.URL_PATROL_RECOR);
        sBuffer.append(sForm);
        sBuffer.append(userId);
        sBuffer.append("/");
        sBuffer.append(lastTimeMillis);
        sBuffer.append("/");
        sBuffer.append(EncodeUtils.urlEncode(msg));
        return RxUtils.request(HttpMethod.GET, sBuffer.toString(), type,params);
    }

    public List<EventEntity> getEventEntitys(){
        return SQLite.select().from(EventEntity.class).where(EventEntity_Table.status.eq(0)).queryList();
    }

    public int getEventEntitysSize(){
        List<EventEntity> eventEntities = getEventEntitys();
        return eventEntities==null ? 0 : eventEntities.size();
    }

    public Observable<MyResponse<List<DeptEntity>>> getProjectList(long userId){
        Type type = new TypeToken<MyResponse<List<DeptEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        StringBuffer url = new StringBuffer( Urls.URL_PROJECT_LIST);
        url.append(userId);
        return RxUtils.request(HttpMethod.GET, url.toString(), type,params);
    }

}
