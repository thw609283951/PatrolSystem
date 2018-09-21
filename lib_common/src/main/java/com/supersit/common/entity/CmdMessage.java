package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class CmdMessage<T> implements Serializable{

    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private JsonObject data;

    public CmdMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
