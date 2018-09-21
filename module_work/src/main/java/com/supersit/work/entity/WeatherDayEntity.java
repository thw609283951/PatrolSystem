package com.supersit.work.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 田皓午 on 2018/5/6.
 */

public class WeatherDayEntity implements Parcelable{

    @SerializedName("date")
    private String date;
    @SerializedName("high")
    private String high;
    @SerializedName("low")
    private String low;
    @SerializedName("type")
    private String type;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.high);
        dest.writeString(this.low);
        dest.writeString(this.type);
    }

    public WeatherDayEntity() {
    }

    protected WeatherDayEntity(Parcel in) {
        this.date = in.readString();
        this.high = in.readString();
        this.low = in.readString();
        this.type = in.readString();
    }

    public static final Creator<WeatherDayEntity> CREATOR = new Creator<WeatherDayEntity>() {
        @Override
        public WeatherDayEntity createFromParcel(Parcel source) {
            return new WeatherDayEntity(source);
        }

        @Override
        public WeatherDayEntity[] newArray(int size) {
            return new WeatherDayEntity[size];
        }
    };
}
