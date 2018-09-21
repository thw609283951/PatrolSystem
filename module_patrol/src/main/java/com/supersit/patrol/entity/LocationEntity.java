package com.supersit.patrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Administrator on 2018/3/5.
 */

public class LocationEntity extends BaseModel implements Parcelable{


    private double latitude;
    private double longitude;

    private String address;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.address);
    }

    public LocationEntity() {
    }

    protected LocationEntity(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
    }

    public static final Creator<LocationEntity> CREATOR = new Creator<LocationEntity>() {
        @Override
        public LocationEntity createFromParcel(Parcel source) {
            return new LocationEntity(source);
        }

        @Override
        public LocationEntity[] newArray(int size) {
            return new LocationEntity[size];
        }
    };
}
