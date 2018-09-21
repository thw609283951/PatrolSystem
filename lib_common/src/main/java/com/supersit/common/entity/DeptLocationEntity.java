package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.db.CommonDatabase;

/**
 * Created by Administrator on 2018/3/5.
 */

@Table(database = CommonDatabase.class)
public class DeptLocationEntity extends BaseModel implements Parcelable{

    @PrimaryKey
    @SerializedName("deptId")
    private long id;

    @Column
    @SerializedName("deptX")
    private double latitude;

    @Column
    @SerializedName("deptY")
    private double longitude;

    @Column
    @SerializedName("deptAddress")
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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


    public DeptLocationEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
    }

    protected DeptLocationEntity(Parcel in) {
        this.id = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
    }

    public static final Creator<DeptLocationEntity> CREATOR = new Creator<DeptLocationEntity>() {
        @Override
        public DeptLocationEntity createFromParcel(Parcel source) {
            return new DeptLocationEntity(source);
        }

        @Override
        public DeptLocationEntity[] newArray(int size) {
            return new DeptLocationEntity[size];
        }
    };
}
