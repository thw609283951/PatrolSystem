package com.supersit.common.model;

import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperDelegate;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.supersit.common.R;
import com.supersit.common.base.BaseUrls;
import com.supersit.common.db.CommonDatabase;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.DeptEntity_Table;
import com.supersit.common.entity.DeptLocationEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.entity.UserEntity_DeptEntity;
import com.supersit.common.entity.UserEntity_DeptEntity_Table;
import com.supersit.common.entity.UserEntity_Table;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.utils.GsonUtil;
import com.supersit.common.utils.MyUtil;
import com.supersit.common.utils.RxUtils;
import com.supersit.mvp.model.SuperModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by linlongxin on 2016/8/7.
 */

public class UserModel extends SuperModel {
    protected String TAG = UserModel.class.getSimpleName();


    private static String SHARED_KEY_CURRENTUSER_USERID = "SHARED_KEY_CURRENTUSER_USERID";
    private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
    private static String SHARED_KEY_CURRENTUSER_NICKNAME = "SHARED_KEY_CURRENTUSER_NICKNAME";
    private static String SHARED_KEY_CURRENTUSER_AVATARURL = "SHARED_KEY_CURRENTUSER_AVATARURL";
    private static String SHARED_KEY_CURRENTUSER_MODEPHONE = "SHARED_KEY_CURRENTUSER_MODEPHONE";
    private static String SHARED_KEY_CURRENTUSER_DEPARTMENT = "SHARED_KEY_CURRENTUSER_DEPARTMENT";
    private static String SHARED_KEY_CURRENTUSER_POSITIONS = "SHARED_KEY_CURRENTUSER_POSITIONS";

    private UserEntity mCurrUser;
    private List<UserEntity> mUserEntitys;
    private List<DeptEntity> mDeptEntitiys;

    public UserModel() {
    }

    public static UserModel getInstance() {
        return getInstance(UserModel.class);
    }

    public long getCurrUserId(){
        if(null == mCurrUser)
            mCurrUser = getCurrUser();
        return null == mCurrUser? -1 :mCurrUser.getUserId();
    }

    public String getCurrUserName(){
        if(null == mCurrUser)
            mCurrUser = getCurrUser();
        return null == mCurrUser? "-1" :mCurrUser.getUserName();
    }

    public UserEntity getCurrUser() {
        if(null == mCurrUser){
            mCurrUser = new UserEntity();
            mCurrUser.setUserId(getLong(SHARED_KEY_CURRENTUSER_USERID,-1l));
            mCurrUser.setUserName(getString(SHARED_KEY_CURRENTUSER_USERNAME,null));
            mCurrUser.setNickName(getString(SHARED_KEY_CURRENTUSER_NICKNAME,null));
            mCurrUser.setAvatarUrl(getString(SHARED_KEY_CURRENTUSER_AVATARURL,null));
            mCurrUser.setModePhone(getString(SHARED_KEY_CURRENTUSER_MODEPHONE,null));

            String sDepts = getString(SHARED_KEY_CURRENTUSER_DEPARTMENT,null);
            if(null != sDepts){
                List<DeptEntity> depts = GsonUtil.fromJson(sDepts,new TypeToken<List<DeptEntity>>(){}.getType());
                mCurrUser.setDepts(depts);
            }
            mCurrUser.setPositions(getString(SHARED_KEY_CURRENTUSER_POSITIONS,null));
        }
        return mCurrUser;
    }

    public void setCurrUser(UserEntity currUser) {
        this.mCurrUser = currUser;
        putLong(SHARED_KEY_CURRENTUSER_USERID,currUser.getUserId());
        putString(SHARED_KEY_CURRENTUSER_USERNAME,currUser.getUserName());
        putString(SHARED_KEY_CURRENTUSER_NICKNAME,currUser.getNickName());
        putString(SHARED_KEY_CURRENTUSER_AVATARURL,currUser.getAvatarUrl());
        putString(SHARED_KEY_CURRENTUSER_MODEPHONE,currUser.getModePhone());

        if(null != currUser.getDepts() && !currUser.getDepts().isEmpty()){
            String sDepts = new Gson().toJson(currUser.getDepts()); ;
            putString(SHARED_KEY_CURRENTUSER_DEPARTMENT,sDepts);
        }
        putString(SHARED_KEY_CURRENTUSER_POSITIONS,currUser.getPositions());
    }


    public Observable<MyResponse<List<UserEntity>>> getUsersByServer(long userId,long timeMillis){
        Type type = new TypeToken<MyResponse<List<UserEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        String url = new StringBuffer(BaseUrls.URL_FRIEND_LIST)
                .append(userId).append("/").append(timeMillis).toString();
        return RxUtils.request(HttpMethod.GET, url, type,params);
    }

    public Observable<MyResponse<List<DeptEntity>>> getDeptsByServer(long userId,long timeMillis){
        Type type = new TypeToken<MyResponse<List<DeptEntity>>>() {}.getType();
        HttpParams params = new HttpParams();
        String url = new StringBuffer(BaseUrls.URL_DEPT_LIST)
                .append(userId).append("/").append(timeMillis).toString();
        return RxUtils.request(HttpMethod.GET, url, type,params);
    }

    public Observable<MyResponse> saveUser_DeptsRelations(boolean isNewData, List<UserEntity> userEntitys){
        return  Observable.create(emitter -> {
            if(null != userEntitys){
                if(!isNewData){
                    for (UserEntity userEntity : userEntitys) {
                        deleteUserEntity_DeptEntityByUserId(userEntity.getUserId());
                    }
                }

                List<UserEntity_DeptEntity> userEntity_deptEntitys = new ArrayList<>();
                for (UserEntity userEntity : userEntitys) {
                    List<DeptEntity> deptEntitiys = userEntity.getDepts();
                    if(null != deptEntitiys && !deptEntitiys.isEmpty()){
                        for (DeptEntity deptEntitiy : deptEntitiys) {
                            UserEntity_DeptEntity userEntity_deptEntitiy = new UserEntity_DeptEntity();
                            userEntity_deptEntitiy.setUserEntity(userEntity);
                            userEntity_deptEntitiy.setDeptEntity(deptEntitiy);

                            userEntity_deptEntitys.add(userEntity_deptEntitiy);
                        }
                    }
                }
                saveUsers_Depts(isNewData,userEntity_deptEntitys);
            }


            MyResponse<UserEntity> myResponse = new MyResponse<>();
            myResponse.setSuccess(true);
            myResponse.setMsg(Utils.getApp().getString(R.string.load_success));
            emitter.onNext(myResponse);
            emitter.onComplete();
        });
    }

    public void saveUsers_Depts(boolean isNewData, List<UserEntity_DeptEntity> userEntity_deptEntitys){
       if(isNewData)
            Delete.table(UserEntity_DeptEntity.class);

       FlowManager.getDatabase(CommonDatabase.class)
                .executeTransaction(new ProcessModelTransaction.Builder<UserEntity_DeptEntity>(
                        BaseModel::save
                ).addAll(userEntity_deptEntitys).build());
    }

    public void saveUsers(boolean isNewData, List<UserEntity> userEntitys){
        if(null == userEntitys || userEntitys.isEmpty()) return;

        for (UserEntity userEntity : userEntitys) {
            userEntity.setInitialLetter(MyUtil.getStrInitialLetter( null == userEntity.getNickName()? userEntity.getUserName() : userEntity.getNickName()));
        }
        if(isNewData){
            Delete.table(UserEntity.class);
            mUserEntitys = userEntitys;
        }else{
            mUserEntitys = null;
        }
        FlowManager.getDatabase(CommonDatabase.class)
                .executeTransaction(new ProcessModelTransaction.Builder<UserEntity>(
                        BaseModel::save
                ).addAll(userEntitys).build());
    }

    public void saveDepts(boolean isNewData, List<DeptEntity> deptEntitys){
        if(null == deptEntitys || deptEntitys.isEmpty()) return;

        if(isNewData){
            mDeptEntitiys =deptEntitys;
            Delete.table(DeptEntity.class);
            Delete.table(DeptLocationEntity.class);
        }else{
            mDeptEntitiys = null;
        }

        FlowManager.getDatabase(CommonDatabase.class)
                .executeTransaction(new ProcessModelTransaction.Builder<DeptEntity>(
                        BaseModel::save
                ).addAll(deptEntitys).build());

//        if(null != deptEntitys && !deptEntitys.isEmpty()){
//            List<DeptLocationEntity> deptLocationEntitys = new ArrayList<>();
//            for (DeptEntity deptEntitiy : deptEntitys) {
//                DeptLocationEntity deptLocationEntity = deptEntitiy.getDeptLocationEntity();
//                if(null != deptLocationEntity){
//                    deptLocationEntitys.add(deptLocationEntity);
//                }
//            }
//            FlowManager.getDatabase(CommonDatabase.class)
//                    .executeTransaction(new ProcessModelTransaction.Builder<DeptLocationEntity>(
//                            BaseModel::save
//                    ).addAll(deptLocationEntitys).build());
//
//            FlowManager.getDatabase(CommonDatabase.class)
//                    .executeTransaction(new ProcessModelTransaction.Builder<DeptEntity>(
//                            BaseModel::save
//                    ).addAll(deptEntitys).build());
//        }
        
    }

    public void deleteDept(long deptId){
        SQLite.delete()
                .from(DeptEntity.class)
                .where(DeptEntity_Table.id.eq(deptId))
                .query();
    }

    public DeptEntity getDeptById(long deptId){
        return SQLite.select()
                .from(DeptEntity.class)
                .where(DeptEntity_Table.id.eq(deptId))
                .querySingle();
    }

    public List<DeptEntity> getDepts(){
        if(null == mDeptEntitiys)
            mDeptEntitiys =SQLite.select().from(DeptEntity.class).queryList();
        return mDeptEntitiys;
    }

    public List<UserEntity> getUsers(){
        if(null == mUserEntitys)
            mUserEntitys =SQLite.select().from(UserEntity.class).queryList();
        return mUserEntitys;
    }

    public UserEntity getUserByUserId(long userId){
        return SQLite.select()
                .from(UserEntity.class)
                .where(UserEntity_Table.userId.eq(userId))
                .querySingle();
    }

    public UserEntity getUserByUserName(String userName){
        return SQLite.select()
                .from(UserEntity.class)
                .where(UserEntity_Table.userName.eq(userName))
                .querySingle();
    }

    public void saveUser(UserEntity userEntity){
        deleteUserEntity_DeptEntityByUserId(userEntity.getUserId());
        userEntity.setInitialLetter(MyUtil.getStrInitialLetter( null == userEntity.getNickName()? userEntity.getUserName() : userEntity.getNickName()));
        userEntity.save();
        saveUserEntity_DeptEntity(userEntity);
    }

    public void deleteUser(long userId){
        SQLite.delete()
                .from(UserEntity.class)
                .where(UserEntity_Table.userId.eq(userId))
                .query();
        deleteUserEntity_DeptEntityByUserId(userId);
    }

    public void saveUserEntity_DeptEntity(UserEntity userEntity){
        List<DeptEntity> deptEntitiys = userEntity.getDepts();
        if(null != deptEntitiys && !deptEntitiys.isEmpty()){
            for (DeptEntity deptEntitiy : deptEntitiys) {
                UserEntity_DeptEntity userEntity_deptEntitiy = new UserEntity_DeptEntity();
                userEntity_deptEntitiy.setUserEntity(userEntity);
                userEntity_deptEntitiy.setDeptEntity(deptEntitiy);
                userEntity_deptEntitiy.save();
            }
        }
    }

    public void deleteUserEntity_DeptEntityByUserId(long userId){
        List<UserEntity_DeptEntity> userEntity_deptEntities = SQLite.select()
                .from(UserEntity_DeptEntity.class)
                .where(UserEntity_DeptEntity_Table.userEntity_userId.eq(userId))
                .queryList();
        if(null != userEntity_deptEntities)
            for (UserEntity_DeptEntity userEntity_deptEntity : userEntity_deptEntities) {
                userEntity_deptEntity.delete();
            }
    }

    synchronized public void reset(){
        mCurrUser = null;
        mUserEntitys = null;
    }
}
