package com.supersit.work.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 田皓午 on 2018/5/6.
 */


public class WeatherEntity implements Parcelable{

    @SerializedName("yesterday")
    private WeatherDayEntity yesterday;

    @SerializedName("city")
    private String city;

    @SerializedName("forecast")
    private List<WeatherDayEntity> forecast;

    @SerializedName("ganmao")
    private String hint;

    @SerializedName("wendu")
    private int temperature;


    public WeatherDayEntity getYesterday() {
        return yesterday;
    }

    public void setYesterday(WeatherDayEntity yesterday) {
        this.yesterday = yesterday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<WeatherDayEntity> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherDayEntity> forecast) {
        this.forecast = forecast;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.yesterday, flags);
        dest.writeString(this.city);
        dest.writeTypedList(this.forecast);
        dest.writeString(this.hint);
        dest.writeInt(this.temperature);
    }

    public WeatherEntity() {
    }

    protected WeatherEntity(Parcel in) {
        this.yesterday = in.readParcelable(WeatherDayEntity.class.getClassLoader());
        this.city = in.readString();
        this.forecast = in.createTypedArrayList(WeatherDayEntity.CREATOR);
        this.hint = in.readString();
        this.temperature = in.readInt();
    }

    public static final Creator<WeatherEntity> CREATOR = new Creator<WeatherEntity>() {
        @Override
        public WeatherEntity createFromParcel(Parcel source) {
            return new WeatherEntity(source);
        }

        @Override
        public WeatherEntity[] newArray(int size) {
            return new WeatherEntity[size];
        }
    };
}
