package com.supersit.work.model;

import com.blankj.utilcode.util.EncodeUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.supersit.common.base.BaseUrls;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.EventHandleEntity;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.utils.RxUtils;
import com.supersit.mvp.model.SuperModel;
import com.supersit.work.db.WorkDatabase;
import com.supersit.work.entity.ApplicationEntity;
import com.supersit.work.entity.NotificationEntity;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 田皓午 on 2018/4/1.
 */

public class WorkModel extends SuperModel{

    private static String SHARED_KEY_APPS_SYNC_LASTTIME = "SHARED_KEY_APPS_SYNC_LASTTIME";

    private Boolean mIsAppsSynced = null;
    private Long mSycnAppsTimeMillis = null;
    private List<ApplicationEntity> mApplicationEntitys=null;

    public WorkModel() {
    }

    @Override
    public void reset() {
        mApplicationEntitys=null;
        setAppsSynced(false);
        setLastSycnAppsTimeMillis(-1);
    }

    public static WorkModel getInstance() {
        return getInstance(WorkModel.class);
    }


    public boolean isAppsSynced() {
        if(null == mIsAppsSynced)
            mIsAppsSynced = false;
        return mIsAppsSynced;
    }

    public void setAppsSynced(boolean isSyncedWithServer) {
        mIsAppsSynced = isSyncedWithServer;
    }

    public void setLastSycnAppsTimeMillis(long timeMillis){
        mSycnAppsTimeMillis = timeMillis;
        putLong(SHARED_KEY_APPS_SYNC_LASTTIME,timeMillis);
    }

    public long getLastSycnAppsTimeMillis(){
        if(null == mSycnAppsTimeMillis)
            mSycnAppsTimeMillis = getLong(SHARED_KEY_APPS_SYNC_LASTTIME,-1);
        return mSycnAppsTimeMillis;
    }

    public Observable<MyResponse<JsonObject>> getWorkEventsSize(long userId){
        Type type = new TypeToken<MyResponse<JsonObject>>() {}.getType();
        HttpParams params = new HttpParams();
        StringBuffer url = new StringBuffer(Urls.URL_WORK_EVENTS);
        url.append(userId);
        return RxUtils.request(HttpMethod.GET, url.toString(), type,params);
    }

    public Observable<MyResponse<List<EventEntity>>> getWorkEventsByServer(long userId,int status,long lastTimeMillis,String msg ){
        Type type = new TypeToken<MyResponse<List<EventEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        if(0 == lastTimeMillis){
            lastTimeMillis = System.currentTimeMillis();
        }
        if(null == msg || msg.trim().isEmpty()){
            msg = "-1";
        }
        StringBuffer url = new StringBuffer( Urls.URL_WORK_EVENTS);
        url.append(userId);
        url.append("/");
        url.append(status);
        url.append("/");
        url.append(lastTimeMillis);
        url.append("/");
        url.append(EncodeUtils.urlEncode(msg));

        return RxUtils.request(HttpMethod.GET, url.toString(), type,params);
    }

    public Observable<MyResponse<List<ApplicationEntity>>> getApplicationEntitysByServer(long userId,long timeMillis){
        Type type = new TypeToken<MyResponse<List<ApplicationEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        String url = new StringBuffer(Urls.URL_APP_LIST)
                .append(userId).append("/").append(timeMillis).toString();
        return RxUtils.request(HttpMethod.GET, url, type,params);
    }


    public Observable<MyResponse<List<NotificationEntity>>> getNotificationsByServer(long userId,long lastTimeMillis){
        Type type = new TypeToken<MyResponse<List<NotificationEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        if(0 == lastTimeMillis){
            lastTimeMillis = System.currentTimeMillis();
        }
        StringBuffer url = new StringBuffer( Urls.URL_WORK_NOTIFYS);
        url.append(userId);
        url.append("/");
        url.append(lastTimeMillis);

        return RxUtils.request(HttpMethod.GET, url.toString(), type,params);
    }


    public List<ApplicationEntity> getApplicationEntitys(){
        if(null ==mApplicationEntitys)
            mApplicationEntitys = SQLite.select().from(ApplicationEntity.class).queryList();
        return mApplicationEntitys;
    }
    public void saveApplicationEntitys(boolean isNewData,List<ApplicationEntity> applicationEntities){;
        if(isNewData){
            mApplicationEntitys =applicationEntities;
            Delete.table(ApplicationEntity.class);
        }else{
            mApplicationEntitys = null;
        }

        FlowManager.getDatabase(WorkDatabase.class)
                .executeTransaction(new ProcessModelTransaction.Builder<ApplicationEntity>(
                        BaseModel::save
                ).addAll(applicationEntities).build());
    }

    //上传事件处理信息
    public Observable<MyResponse> upLoadEventHandleEntity(EventHandleEntity eventEntity, List<File> files){
        Type type = new TypeToken<MyResponse>() {}.getType();
        HttpParams params = new HttpParams();
        params.put("receiveUserId",eventEntity.getReceiveUserId());
        params.put("sendUserId",eventEntity.getSendUserId());
        params.put("workpastId",eventEntity.getId());
        params.put("workpastFlowTime",eventEntity.getTime());
        params.put("workpastStatus",eventEntity.getStatus());
        params.put("workpastFlowContent",EncodeUtils.urlEncode(eventEntity.getMessage()));

        return RxUtils.request(HttpMethod.POST, Urls.URL_EVENT_HANDLE, type,params);
    }

    /**
     *  获取天气信息
     * @param cityCode
     * @return
     */
    public Observable<JsonObject> getWeatherInfo(long cityCode){
        Type type = new TypeToken<JsonObject>() {}.getType();
        HttpParams params = new HttpParams();
        params.put("citykey",cityCode);
        return RxUtils.request(HttpMethod.GET, Urls.URL_WEATHER, type,params);
    }


    /**
     * 获取巡查动态信息
     * @param userId
     * @return
     */
    public Observable<MyResponse<List<EventEntity>>> getPatrolDynamicByServer(long userId){
        Type type = new TypeToken<MyResponse<List<EventEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        return RxUtils.request(HttpMethod.GET, Urls.URL_PATROL_DYNAMIC+userId, type,params);
    }


    public List<NotificationEntity> getNotifications(){
        return SQLite.select().from(NotificationEntity.class).queryList();
    }
}
