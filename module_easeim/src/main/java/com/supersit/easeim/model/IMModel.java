package com.supersit.easeim.model;

import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.entity.UserEntity_Table;
import com.supersit.common.model.CommonModel;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.model.MyResponse;
import com.supersit.easeim.IMHelper;
import com.supersit.easeim.R;
import com.supersit.mvp.model.SuperModel;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by 田皓午 on 2018/4/1.
 */

public class IMModel extends SuperModel{
    private static final String TAG = IMModel.class.getSimpleName();

    private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
    private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
    private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
    private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";

    private static String SHARED_KEY_GROUPS_SYNCED = "SHARED_KEY_SETTING_GROUPS_SYNCED";
//    private static String SHARED_KEY_CONTACT_SYNCED = "SHARED_KEY_SETTING_CONTACT_SYNCED";
    private static String SHARED_KEY_CONTACT_SYNC_LASTTIME = "SHARED_KEY_CONTACT_SYNC_LASTTIME";
    private static String SHARED_KEY_DEPT_SYNC_LASTTIME = "SHARED_KEY_DEPT_SYNC_LASTTIME";

    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();

    private Boolean mIsGroupsSyncedWithServe = null;
    private Boolean mIsContactsSyncedWithServer = null;
    private Long mSycnContactTimeMillis = null;
    private Long mSycnDeptTimeMillis = null;

    public IMModel() {
    }

    @Override
    public void reset() {
        setGroupsSynced(false);
        setContactSynced(false);
        setLastSycnContactTimeMillis(-1);
        setLastSycnDeptTimeMillis(-1);
    }

    public static IMModel getInstance() {
        return getInstance(IMModel.class);
    }

    public boolean isLoggedIn(){
        return EMClient.getInstance().isLoggedInBefore();
    }

    public Observable<MyResponse> loginEaseIM(String username, String password, final UserEntity userEntity){
        return  Observable.create(emitter -> {
            EMClient.getInstance().login(username, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "login: onSuccess");
                    // ** manually load all local groups and conversation
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
//                    UserModel.getInstance().setCurrUser(userEntity);

                    MyResponse<UserEntity> myResponse = new MyResponse<>();
                    myResponse.setSuccess(true);
                    myResponse.setMsg(Utils.getApp().getString(R.string.login_success));
                    emitter.onNext(myResponse);
                    emitter.onComplete();
                }

                @Override
                public void onError(final int code, final String message) {
                    emitter.onError(new Throwable(message));
                    emitter.onComplete();
                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        });
    }

    public Observable<MyResponse> logoutEaseIM(){
        return  Observable.create(emitter -> {
            IMHelper.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    MyResponse<UserEntity> myResponse = new MyResponse<>();
                    myResponse.setSuccess(true);
                    myResponse.setMsg(Utils.getApp().getString(R.string.load_success));
                    emitter.onNext(myResponse);
                    emitter.onComplete();
                }

                @Override
                public void onError(int i, String s) {
                    emitter.onError(new Throwable(Utils.getApp().getString(R.string.load_fail)));
                    emitter.onComplete();
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        });
    }

    public EaseUser getEaseUserByUserName(String userName){
        EaseUser easeUser = null;
        UserEntity user = SQLite.select()
                .from(UserEntity.class)
                .where(UserEntity_Table.userName.eq(userName))
                .querySingle();
        if(null != user){
            easeUser = new EaseUser(userName);
            easeUser.setNickname(user.getNickName());
            easeUser.setAvatar(user.getAvatarUrl());
            easeUser.setInitialLetter(user.getInitialLetter());
        }
        return easeUser;
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);
        if(val == null){
            val = getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = getBoolean(SHARED_KEY_SETTING_SOUND, true);
            valueCache.put(Key.PlayToneOn, val);
        }
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null?val:true);
    }

    public void setGroupsSynced(boolean synced){
        mIsGroupsSyncedWithServe = synced;
        putBoolean(SHARED_KEY_GROUPS_SYNCED, synced);
    }

    public boolean isGroupsSynced(){
        if(null == mIsGroupsSyncedWithServe)
            mIsGroupsSyncedWithServe = getBoolean(SHARED_KEY_GROUPS_SYNCED, false);
        return mIsGroupsSyncedWithServe;
    }

    public void setContactSynced(boolean synced){
        mIsContactsSyncedWithServer = synced;
//        putBoolean(SHARED_KEY_CONTACT_SYNCED, synced);
    }

    public boolean isContactSynced(){
        if(null == mIsContactsSyncedWithServer)
            mIsContactsSyncedWithServer=false;
//            mIsContactsSyncedWithServer = getBoolean(SHARED_KEY_CONTACT_SYNCED, false);
        return mIsContactsSyncedWithServer;
    }

    public void setLastSycnContactTimeMillis(long timeMillis){
        mSycnContactTimeMillis = timeMillis;
       putLong(SHARED_KEY_CONTACT_SYNC_LASTTIME,timeMillis);
    }

    public long getLastSycnContactTimeMillis(){
        if(null == mSycnContactTimeMillis)
            mSycnContactTimeMillis = getLong(SHARED_KEY_CONTACT_SYNC_LASTTIME,-1);
        return mSycnContactTimeMillis;
    }

    public void setLastSycnDeptTimeMillis(long timeMillis){
        mSycnDeptTimeMillis = timeMillis;
        putLong(SHARED_KEY_DEPT_SYNC_LASTTIME,timeMillis);
    }

    public long getLastSycnDeptTimeMillis(){
        if(null == mSycnDeptTimeMillis)
            mSycnDeptTimeMillis = getLong(SHARED_KEY_DEPT_SYNC_LASTTIME,-1);
        return mSycnDeptTimeMillis;
    }

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
