package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.model.CommonModel;

/**
 * Created by 田皓午 on 2018/4/21.
 */

public class EventHandleEntity extends BaseModel implements Parcelable{

    public final static int UPLIAD = 1;
    public final static int ACCEPT = 2;
    public final static int END = 3;


    @SerializedName("workpastId")
    private String id;

    @SerializedName("sendUserId")
    private long sendUserId;

    @SerializedName("receiveUserId")
    private long receiveUserId;

    @SerializedName("user")
    private UserEntity userEntity;

    @SerializedName("receiveUser")
    private UserEntity receiveUserEntity;

    @SerializedName("workpastFlowTime")
    private long time;

    @SerializedName("workpastFlowContent")
    private String message;

    @SerializedName("workpastStatus")
    private int status; // 0：上报 、1:受理 、2:完成

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public UserEntity getUserEntity() {
        if(null == userEntity)
            userEntity = CommonModel.getInstance().getUserByUserId(sendUserId);
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public UserEntity getReceiveUserEntity() {
        if(null == receiveUserEntity)
            receiveUserEntity = CommonModel.getInstance().getUserByUserId(receiveUserId);
        return receiveUserEntity;
    }

    public void setReceiveUserEntity(UserEntity receiveUserEntity) {
        this.receiveUserEntity = receiveUserEntity;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.sendUserId);
        dest.writeLong(this.receiveUserId);
        dest.writeParcelable(this.userEntity, flags);
        dest.writeParcelable(this.receiveUserEntity, flags);
        dest.writeLong(this.time);
        dest.writeString(this.message);
        dest.writeInt(this.status);
    }

    public EventHandleEntity() {
    }

    protected EventHandleEntity(Parcel in) {
        this.id = in.readString();
        this.sendUserId = in.readLong();
        this.receiveUserId = in.readLong();
        this.userEntity = in.readParcelable(UserEntity.class.getClassLoader());
        this.receiveUserEntity = in.readParcelable(UserEntity.class.getClassLoader());
        this.time = in.readLong();
        this.message = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<EventHandleEntity> CREATOR = new Creator<EventHandleEntity>() {
        @Override
        public EventHandleEntity createFromParcel(Parcel source) {
            return new EventHandleEntity(source);
        }

        @Override
        public EventHandleEntity[] newArray(int size) {
            return new EventHandleEntity[size];
        }
    };
}
