package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.db.CommonDatabase;
import com.supersit.common.model.CommonModel;
import com.supersit.common.utils.MyUtil;

import java.util.List;

/**
 * Created by 田皓午 on 2018/4/12.
 */

@Table(database = CommonDatabase.class)
public class EventEntity extends BaseModel implements Parcelable{

    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    @SerializedName("workpastId")
    private String eventId;

    @Column
    @SerializedName("workpastNumber")
    private String number;

    @Column
    @SerializedName("userId")
    private long userId;

    @SerializedName("user")
    private UserEntity userEntity;

    @SerializedName("receiveUser")
    private UserEntity receiveUserEntity;

    @Column
    @SerializedName("deptId")
    private long deptId;

    @SerializedName("dept")
    private DeptEntity deptEntitiy;

    @Column
    @SerializedName("workpastStatus")
    private int status; //默认 1：待受理 、2:处理中 、3:已办结

    @Column
    @SerializedName("workpastTime")
    private long time;

    @Column
    @SerializedName("workpastType")
    private String eventType;

    @Column
    @SerializedName("workpastTitle")
    private String eventTitle;

    @Column
    @SerializedName("workpastIsNormal")
    private int normal;

    @Column
    @SerializedName("workpastRemark")
    private String eventRemark;

    @Column
    @SerializedName("receiveUserId")
    private long receiveUserId;

    @Column
    @SerializedName("workpastGpsX")
    private double latitude;

    @Column
    @SerializedName("workpastGpsY")
    private double longitude;

    @Column
    @SerializedName("workpastAddress")
    private String address;

    @Column
    private String stringMediaUrls;

    @SerializedName("filePathList")
    private List<String> mediaUrls;

    @SerializedName("workpastFlows")
    private List<EventHandleEntity> eventHandleEntitys;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public UserEntity getUserEntity() {
        if(null == userEntity)
            userEntity = CommonModel.getInstance().getUserByUserId(userId);
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

    public List<EventHandleEntity> getEventHandleEntitys() {
        return eventHandleEntitys;
    }

    public void setEventHandleEntitys(List<EventHandleEntity> eventHandleEntitys) {
        this.eventHandleEntitys = eventHandleEntitys;
    }

    public void setReceiveUserEntity(UserEntity receiveUserEntity) {
        this.receiveUserEntity = receiveUserEntity;
    }

    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    public DeptEntity getDeptEntitiy() {
        if(null == deptEntitiy)
            deptEntitiy = CommonModel.getInstance().getDeptEntityById(deptId);
        return deptEntitiy;
    }

    public void setDeptEntitiy(DeptEntity deptEntitiy) {
        this.deptEntitiy = deptEntitiy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public String getEventRemark() {
        return eventRemark;
    }

    public void setEventRemark(String eventRemark) {
        this.eventRemark = eventRemark;
    }

    public long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStringMediaUrls() {
        return stringMediaUrls;
    }

    public void setStringMediaUrls(String stringMediaUrls) {
        this.stringMediaUrls = stringMediaUrls;
    }

    public List<String> getMediaUrls() {
        if(null == mediaUrls){
            mediaUrls = MyUtil.stringToList(",",stringMediaUrls);
        }
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
        if(null != mediaUrls)
            this.stringMediaUrls = MyUtil.listToString(",",mediaUrls);
        else
            this.stringMediaUrls = null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.eventId);
        dest.writeString(this.number);
        dest.writeLong(this.userId);
        dest.writeParcelable(this.userEntity, flags);
        dest.writeParcelable(this.receiveUserEntity, flags);
        dest.writeInt(this.status);
        dest.writeLong(this.time);
        dest.writeString(this.eventType);
        dest.writeString(this.eventTitle);
        dest.writeInt(this.normal);
        dest.writeString(this.eventRemark);
        dest.writeLong(this.receiveUserId);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
        dest.writeString(this.stringMediaUrls);
        dest.writeStringList(this.mediaUrls);
        dest.writeTypedList(this.eventHandleEntitys);
    }

    public EventEntity() {
    }

    protected EventEntity(Parcel in) {
        this.id = in.readLong();
        this.eventId = in.readString();
        this.number = in.readString();
        this.userId = in.readLong();
        this.userEntity = in.readParcelable(UserEntity.class.getClassLoader());
        this.receiveUserEntity = in.readParcelable(UserEntity.class.getClassLoader());
        this.status = in.readInt();
        this.time = in.readLong();
        this.eventType = in.readString();
        this.eventTitle = in.readString();
        this.normal = in.readInt();
        this.eventRemark = in.readString();
        this.receiveUserId = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
        this.stringMediaUrls = in.readString();
        this.mediaUrls = in.createStringArrayList();
        this.eventHandleEntitys = in.createTypedArrayList(EventHandleEntity.CREATOR);
    }

    public static final Creator<EventEntity> CREATOR = new Creator<EventEntity>() {
        @Override
        public EventEntity createFromParcel(Parcel source) {
            return new EventEntity(source);
        }

        @Override
        public EventEntity[] newArray(int size) {
            return new EventEntity[size];
        }
    };
}
